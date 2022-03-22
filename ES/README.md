
## 介绍

Elasticsearch是一个分布式可扩展的实时搜索引擎，建立在全文搜索引擎Apache Lucene基础上的搜索引擎，它不仅包含全文搜索功能，还包括以下功能：

- 分布式实时文件存储，并将每个字段编入索引，使其可以被搜索
- 实时分析的分布式搜索引擎
- 可以扩展到上百台服务器，处理PB级别的结构化或非结构化数据

## 基本概念

Elasticsearch是面向文档型数据库，一条数据在Elasticsearch里就是一个文档，用JSON作为文档序列化的格式。

关系型数据库和Elasticsearch对应关系如下

```
关系数据库       ⇒ 数据库        ⇒ 表         ⇒ 行              ⇒ 列(Columns) `

Elasticsearch  ⇒ 索引(Index)   ⇒ 类型(type)  ⇒ 文档(Docments)  ⇒ 字段(Fields) `
```

一个Elasticsearch可以包含多个索引，也就是说包含了很多类型，每个类型包含了很多文档，然后每个文档又有若干个字段。

## 索引

精髓：一切都是为了提高搜索的性能

### Elasticsearch是如何快速索引的？



#### 倒排索引：

![Alt text](https://img-blog.csdnimg.cn/20210721105315250.png)

现假设有以下一个文档：

```
| ID | Name | Age  |  Sex     |
| -- |:------------:| -----:| -----:| 
| 1  | Kate         | 24 | Female
| 2  | John         | 24 | Male
| 3  | Bill         | 29 | Male
```

ID是Elasticsearch自建的文档id，那么Elasticsearch建立的索引如下：

```
Name:

| Term | Posting List |
| -- |:----:|
| Kate | 1 |
| John | 2 |
| Bill | 3 |
```

```
Age:

| Term | Posting List |
| -- |:----:|
| 24 | [1,2] |
| 29 | 3 |
```

```
Sex:

| Term | Posting List |
| -- |:----:|
| Female | 1 |
| Male | [2,3] |
```

#### Posting List

Elasticsearch分别为每个field都建立了一个倒排索引，Kate, John, 24, Female这些叫term，而[1,2]就是**Posting List**。Posting list就是一个int的数组，存储了所有符合某个term的文档id。

使用Posting List看似可以很快的进行查找，但是如果有大量的记录的话，怎么快速提升查找term的速度呢？

#### Term Dictionary

Elasticsearch为了能快速找到某个term，将所有的term排个序，在内存中二分法查找term，logN的查找效率，就像通过字典查找一样，这就是Term Dictionary。

但是如果term也有很多呢？term dictionary也会很大，放内存不现实，那该如何解决呢？

#### Term Index

类似于字典里的索引页，给term建立索引，term index也是一棵树：

![Alt text](https://img-blog.csdnimg.cn/20210721110912220.png)

这棵树不会包含所有的term，而是包含term的一些前缀，通过term index可以快速定位到term dictionary的某个offset，然后再从这个位置继续往后顺序查找。如下所示：

![Alt text](https://img-blog.csdnimg.cn/20210721111149858.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NoaUx1TWFuWGk=,size_16,color_FFFFFF,t_70)

结合FST的压缩技术，可以将term index存入内存中，这样通过term index查找到对应term dictionary的block位置之后，再去磁盘上找term，大大减少了磁盘随机读写次数。

#### 对于Posting List的压缩

如果Elasticsearch需要对同学的性别进行索引，如果有上百万个同学，但是性别只有男女两种，就会造成每个posting list里面都会有百万数量级的同学id，那么就需要对posting list进行进一步的压缩

##### Frame Of Reference

通过增量编码压缩，将大数变小数，按字节方式存储

根据posting list的有序性，将原来的大小数变成仅储存增量值，再精打细算按照bit排好队，最后按照字节存储。如下图：

![img](https://img-blog.csdnimg.cn/20210721111735826.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NoaUx1TWFuWGk=,size_16,color_FFFFFF,t_70)

##### Roaring bitmaps

bitmap是一种数据结构，假设有某个posting list：[1,3,4,7,10]

对应的bitmap就是：[1,0,1,1,0,0,1,0,0,1]

这样可以用一个字节表示8个文档id，但是这样并不搞笑，如果有1亿个文档，则需要12.5MB的存储空间，再如果有多个索引，存储空间会随着文档个数呈线性增长。

在这样的情况下，就引出了Roaring bitmaps这样更高效的数据结构，它将posting list按照65535为界限分块，比如第一块所包含的文档id范围在0~65535之间，第二块的id范围是65536~131071，以此类推。再用<商，余数>的组合表示每一组id，这样每组里的id范围都在0~65535内了，剩下的就好办了，既然每组id不会变得无限大，那么我们就可以通过最有效的方式对这里的id存储。

![img](https://img-blog.csdnimg.cn/20210721124704769.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NoaUx1TWFuWGk=,size_16,color_FFFFFF,t_70)

之所以用65535分割，是因为它=2^16-1，正好是用2个字节能表示的最大数，一个short的存储单位。注意最后到block级别，如果是大块，用节省点用bitset存，小块就用一个short[]存着方便。

### 联合索引

多field索引的联合查询时，Elasticsearch采用以下两种方式满足快速查找的要求

#### Skip List（跳表）



![Alt text](https://raw.githubusercontent.com/Neway6655/neway6655.github.com/master/images/elasticsearch-study/skiplist.png)

##### 跳表原理

跳表允许快速查询、插入和删除一个有序连续元素的数据链表。快速查询是通过维护一个多层次的链表，且每一层链表中的元素是前一层链表元素的子集。一开始时，算法在最稀疏的层次进行搜索，直至需要查找的元素在该层两个相邻的元素中间。这时，算法将跳转到下一个层次，重复刚才的搜索，直到找到需要查找的元素为止。

跳表是一种空间换时间的数据结构，根据跳表的结构特征，知道跳表的高度为log(n)，查询每一层的链表的时候，需要遍历的节点的个数是k，复杂度为k*log(n)。但是实际上根据二分的思想，每一层跳表最多访问的节点只有两个，所以k = 2，所以跳表的查询时间复杂度为2log(n)，省略常数2，复杂度为log(n)。

跳表进行插入操作时，如果不停的往两个节点之间加入节点，就会造成两个索引之间节点过多的情况，就不符合跳表的结构特性了，所以跳表通过一个随机函数来维护这个平衡，当我们向跳表中插入数据时，根据随机函数决定我们插入到哪一级索引中。理论来讲，一级索引中元素个数应该占原始数据的 50%，二级索引中元素个数占 25%，三级索引12.5% ，一直到最顶层。

##### 跳表在Elasticsearch联合查找的时候的应用

对于多个posting list，需要把它们用AND合并，得出posting list的交集，首先选择最短的posting list，然后从小到大遍历，依赖跳表的特性进行快速移动和查找，找出多个list的并集

注意：跳表一般用于内存中没有对联合查询的filter求出bitset放在内存中的情况，需要在磁盘上对posting list按照跳表进行查找

#### 利用bitset进行按位与

如果查询的 filter 缓存到了内存中（以 bitset 的形式），那么合并就是两个 bitset 的 AND

## 使用Elasticsearch注意事项

- 不需要索引的字段，一定要明确定义出来，因为默认自动创建索引
- 对于String类型的字段，不需要analysis的也需要明确定义出来，因为默认也是会analysis的
- 选择有规律的ID很重要，随机性大的ID不利于查询

