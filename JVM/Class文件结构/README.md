# Class 文件结构 #

## 基本结构 ##
在Java虚拟机规范中，Class文件使用一种类似于C语言结构体的方式进行描述，并且统一使用无符号整数作为基本数据类型u1、u2、u4分别表示无符号占1、2、4个字节的数据。

     ClassFile {
        u4 magic;										//魔数
        u2 minor_version;								//小版本号
        u2 major_version;								//大版本号
        u2 constant_pool_count;							//常量池个数
        cp_info constant_pool[constant_pool_count-1];	//常量池的表项有constant_pool_count-1项
        u2 access_flags;									//类和接口的信息
        u2 this_Class;									//对常量池的索引
        u2 super_Class;									//0或者是对常量池的一个有效索引
        u2 interfaces_count;								//由该类直接实现或者由该接口所扩展的超接口的数量
        u2 interfaces[interfaces_count];					//由该类直接实现或者由该接口所扩展的超接口的索引
        u2 fields_count;									//类的字段个数
        field_info fields[fields_count];					//类的字段信息
        u2 methods_count;								//对该类或者接口中声明的所有方法的总计数
        method_info methods[methods_count];				//对该类或者接口中声明的所有方法的信息
        u2 attributes_count;								//类或者接口所定义的属性的基本信息个数
        attribute_info attributes[attributes_count];		//类或者接口所定义的属性的基本信息
     }




### 魔数 ###
作为Class文件的标志，用来告诉Java虚拟机，这是一个Class文件。它固定为0xCAFEBABE（和 cafe baby很像）

很多类型的文件，如TAR文件、PE文件、甚至网络DHCP报文内部，都有类似的设计手法。

![](https://img-blog.csdnimg.cn/20210324153828979.png)

### Class文件的版本 ###
紧跟着魔数的两个字节是Java的小版本，然后两个字节是Java的大版本，对应关系如下：

<table border="1">
<tr>
<td>大版本（十进制）</td>
<td>小版本</td>
<td>编译器版本</td>
</tr>
<tr>
<td>45</td>
<td>3</td>
<td>1.1</td>
</tr>
<tr>
<td>46</td>
<td>0</td>
<td>1.2</td>
</tr>
<tr>
<td>47</td>
<td>0</td>
<td>1.3</td>
</tr>
<tr>
<td>48</td>
<td>0</td>
<td>1.4</td>
</tr>
<tr>
<td>49</td>
<td>0</td>
<td>1.5</td>
</tr>
<tr>
<td>50</td>
<td>0</td>
<td>1.6</td>
</tr>
<tr>
<td>51</td>
<td>0</td>
<td>1.7</td>
</tr>
<tr>
<td>52</td>
<td>0</td>
<td>1.8</td>
</tr>
<tr>
<td>53</td>
<td>0</td>
<td>1.9</td>
</tr>
<tr>
<td>54</td>
<td>0</td>
<td>10</td>
</tr>
</table>

Java虚拟机向下兼容，高版本可以执行由低版本编译生成的Class文件。

![](https://img-blog.csdnimg.cn/20210324153903639.png)

如图所示，所用版本就是JDK1.8

### 存放所有常量——常量池 ###
在版本号后，紧跟着的是常量池的数量，如图所示，0x2E换算成十进制是32+14=46项。

![](https://img-blog.csdnimg.cn/20210324154716123.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NoaUx1TWFuWGk=,size_16,color_FFFFFF,t_70)

数量之后是若干个常量池表项，每一项以类型、长度、内容或者类型、内容的格式排列

枚举值和格式如下：

![](https://img-blog.csdnimg.cn/20210324160510905.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NoaUx1TWFuWGk=,size_16,color_FFFFFF,t_70)

如图所示，就是一个CONSTANT\_Utf8的常量，由01这个tag打头，之后两个字节代表长度，0x12是18字节，接下来18字节的字符串代表这个常量的实际内容，可以看到，该字符串为java.lang.String。其他常量以此类推。

![](https://img-blog.csdnimg.cn/20210324161120636.png)

很多类型的index值，代表指向常量区对应位置的索引。

对于对象类型，总是以L开头，紧跟类的全限定名，用分号结尾。数组则以“[”作为标记。

很多时候我们可以借助 JDK 提供的 javap 命令直接查看 Class 文件的常量池信息，其实 javap 出来的就是这样分析总结好的。

### Class的访问标记（Access Flag） ###
在常量池后，紧跟着访问标记。该标记使用2字节表示，用于表明该类的访问信息，如public,final,abstract等。

字段取值如下所示：

![](https://img-blog.csdnimg.cn/20210324175153891.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NoaUx1TWFuWGk=,size_16,color_FFFFFF,t_70)

可以发现,这些标志的标志值是错位的，可以通过或来实现共存，比如public final就是 ACC_PUBLIC | ACC_FINAL。

### 当前类、父类和接口 ###
在Class访问标记后，会指定该类的类别，父类类别及实现的接口。格式如下：

	u2			this_class;
	u2			super_class;
	u2			interfaces_count;
	u2			interfaces(interfaces_count)

其中this\_class和super\_class都是2字节无符号整数，指向常量池中一个CONSTANT\_Class，以表示当前类型和父类。由于在Java中只能使用单继承，所以只需要存储一个父类。

由于一个类可以实现多个接口，所以需要以一个数组的形式来存储，如果该类没有实现接口，则interfaces\_count为0


### Class文件的字段 ###
在接口描述后，会有类的字段信息，首先有两个字节表示字段的个数，接着是字段的具体信息，每一个字段为一个field\_into的结构，如下：

	 u2 			fields_count;
     field_info fields[fields_count];

field\_into结构如下：

	field_into{
		u2		access_flags;
		u2		name_index;
		u2		descriptor_index;
		u2		attributes_count;
		attribute_into attributes[attributes_count];
	}	

filed\_into结构各字段意义如下：

1. access\_flags与类的访问标记类似，取值如下：

![](https://img-blog.csdnimg.cn/20210324203327268.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NoaUx1TWFuWGk=,size_16,color_FFFFFF,t_70)

2. 一个2字节整数，指向常量池中的CONSTANT\_Utf8结构，表示字段名称
3. 一个2字节整数，指向常量池中的CONSTANT\_Utf8结构，表示字段类型。
4. 一个字段还可能拥有一系列属性，用于存储更多的额外信息，比如初始值、一些注释等。额外属性个数存入attributes\_count，以常量属性为例，信息结构如下：

   	ConstantValue_attribute{
   		u2		attribute_name_index;
   		u4		attribute_length;
   		u2		constantvalue_index;
   	}
attribute\_name\_index为额外属性的名称，指向常量池的CONSTANT\_Utf8,attribute\_length表示剩余长度，对于常量属性，该值恒为2，constantvalue\_index表示属性值，也指向常量池的CONSTANT_Utf8

### Class文件的方法基本结构 ###
在字段信息之后，就是类的方法信息。方法信息和类字段信息整体十分类似。结构如下：

	u2 			methods_count;
	method_info	methods[methods_count]

methods\_count表示方法个数，method\_info结构如下：

	method_info{
		u2			access_flag;
		u2			name_index;
		u2			descriptor_index;
		u2			attributes_count;
		attribute_into attributes[attributes_count];
	}

access\_flag取值如下：

![](https://img-blog.csdnimg.cn/2021032420535721.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NoaUx1TWFuWGk=,size_16,color_FFFFFF,t_70)

name\_index和descriptor\_index与字段信息类似

比较重要的属性是Code属性，也就是方法的执行主体，结构如下：

	Code_attribute{
		u2 attribute_name_index;
		u4 attriubute_length;
		u2 max_stack;
		u2 max_locals;
		u4 code_length;
		u1 code[code_length];
		u2 execption_table_length;
		{
			u2 start_pc;				//偏移量开始点
			u2 end_pc;				//偏移量结束点
			u2 handler_pc;			//遇到cathch_type指定的异常，跳到该位置执行
			u2 catch_type;
		}exception_table[exception_table_length];
		u2 attributes_count;
		attribute_info attributes[attributes_count];
	}

1. Code属性的第一个字段attribute\_name\_index对于Code属性，该值指向CONSTANT\_Utf8中，恒为“Code”
2. attribute\_length指定了Code属性的长度，长度不包括前6字节，仅含剩余长度。
3. max\_stack表示操作数栈的最大深度
4. max\_locals表示局部变量表的最大值
5. code\_length表示字节码的长度
6. code[code\_length]为byte数组，为字节码内容本身
7. execptino\_table\_length表示异常处理表
8. Code属性还包含很多其他信息，比如行号、局部变量表等，这些信息都以属性的形式内嵌在Code属性中