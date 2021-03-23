# 内存模型 #
![](https://img-blog.csdnimg.cn/img_convert/dfd68d145bd64be101119115b787b718.png)

1. 类加载子系统负责从文件系统或者网络中加载class信息，加载的类信息存放于一块称为方法区的内存空间。除了类信息外，方法区中可能还存放运行时常量池信息，包括字符串字面量和数字常量（这部分常量信息是class文件常量池部分的内存映射）
2. Java堆在虚拟机启动的时候建立，它是Java程序最主要的内存工作区域，几乎所有Java对象实例都存放于Java堆中，堆空间是所有线程共享的。
3. Java的NIO库允许Java程序使用直接内存。直接内存是在Java堆外的、直接向系统申请的内存区域，通常访问直接内存的速度会优于Java堆。
4. 垃圾回收系统是Java虚拟机的重要组成部分，可以对方法区、Java堆和直接内存进行回收。
5. 每一个Java虚拟机线程都有一个私有的Java栈，一个线程的Java栈在线程创建的时候被创建。Java栈中保存着帧信息，返回值、局部变量、操作数栈、方法参数指针等。
6. 本地方法栈和Java栈很相似，不同在于Java栈用于Java方法的调用，而本地方法栈则用于本地方法的调用，Java虚拟机允许Java直接调用本地方法（通常使用C语言编写）。
7. PC寄存器也是每个线程私有的空间，Java虚拟机会为每一个Java线程创建PC寄存器。在任意时刻，一个Java线程总是在执行一个方法，这个正在被执行的方法被称为当前方法。
如果当前方法不是本地方法，PC寄存器就会指向当前正在被执行的指令。如果当前方法是本地方法，那么PC寄存器的值就是undefined。
8. 执行引擎是Java虚拟机最核心的组件之一，负责执行虚拟机的字节码。

![](https://img-blog.csdnimg.cn/20210315145743390.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NoaUx1TWFuWGk=,size_16,color_FFFFFF,t_70)
## 栈上分配 ##
栈上分配是Java虚拟机提供的一项优化技术，它的基本思想是，对于那些线程私有的对象（这里指不可能被其他线程访问的对象），可以将它们打散分配在栈上，而不是分配在堆上

分配在栈上的好处是可以再函数调用结束后自行销毁，而不需要垃圾回收机制的接入，从而提高系统性能。

栈上分配的一个技术基础是逃逸分析。逃逸分析的目的是判断对象的作用域是否有可能逃逸出函数体。如一个对象是类成员变量，那么就有可能被任何线程访问，因此属于逃逸对象；
而一个对象如果是局部变量，并且该对象没有被返回，或者出现任何形式地公开。因此，它并未发生逃逸，对于这种情况，虚拟机就有可能将该对象分配在栈上而不是堆上。

如程序OnStackTest程序，使用参数

`-server -Xmx10m -Xms10m -XX:+DoEscapeAnalysis -XX:+PrintGC -XX:-UseTLAB -XX:+EliminateAllocations`

-XX:+DoEscapeAnalysis是开启逃逸分析（默认打开），只能在Server模式下启用

-XX:EliminateAllocations是开启了标量替换（默认打开），允许将对象打散分配在栈上，如对象User有id和name两个字段，这两个字段会被视为两个独立的局部变量进行分配

运行后得只有两次GC处理，如果不使用标量替换或者关闭逃逸分析，则会进行大量GC操作，这说明栈上分配依赖于逃逸分析和标量替换的实现。

对于大量的零散小对象，栈上分配提供了一种很好的对象分配策略，这样的策略分配快，且可以有效避免垃圾回收带来的负面影响，但由于和堆空间相比，栈空间较小，因此大对象不适合在栈上分配。


## 最大堆和初始堆设置 ##
HeapAlloc运行结果如下：

    -XX:InitialHeapSize=5242880 -XX:MaxHeapSize=20971520 -XX:+PrintCommandLineFlags -XX:+PrintGCDetails -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:-UseLargePagesIndividualAllocation -XX:+UseSerialGC
    [GC (Allocation Failure) [DefNew: 1664K->192K(1856K), 0.0024222 secs] 1664K->671K(5952K), 0.0024973 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
    maxMemory=20316160 bytes
    free mem=5057600 bytes
    total mem=6094848 bytes
    分配了1MB空间给数组
    maxMemory=20316160 bytes
    free mem=4009008 bytes
    total mem=6094848 bytes
    [GC (Allocation Failure) [DefNew: 1557K->58K(1856K), 0.0019606 secs][Tenured: 1694K->1753K(4096K), 0.0023189 secs] 2036K->1753K(5952K), [Metaspace: 3354K->3354K(1056768K)], 0.0043371 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
    分配了4MB空间给数组
    maxMemory=20316160 bytes
    free mem=4284464 bytes
    total mem=10358784 bytes
    Heap
    def new generation   total 1920K, used 100K [0x00000000fec00000, 0x00000000fee10000, 0x00000000ff2a0000)
    eden space 1728K,   5% used [0x00000000fec00000, 0x00000000fec19018, 0x00000000fedb0000)
    from space 192K,   0% used [0x00000000fedb0000, 0x00000000fedb0000, 0x00000000fede0000)
    to   space 192K,   0% used [0x00000000fede0000, 0x00000000fede0000, 0x00000000fee10000)
    tenured generation   total 8196K, used 5849K [0x00000000ff2a0000, 0x00000000ffaa1000, 0x0000000100000000)
    the space 8196K,  71% used [0x00000000ff2a0000, 0x00000000ff856648, 0x00000000ff856800, 0x00000000ffaa1000)
    Metaspace   used 3433K, capacity 4500K, committed 4864K, reserved 1056768K
    class spaceused 372K, capacity 388K, committed 512K, reserved 1048576K




我们发现，最大内存20MB = 20 * 1024 * 1024 = 20971520，但是实际上打印的最大可用内存仅为20316160，比设定值少。

这是由于垃圾回收的需要，虚拟机会对堆进行分区管理，不同的区采用不同的回收算法，因此会存在可用内存的损失。


具体结果就是实际可用内存会浪费大小等于from/to的空间，但是发现from的大小为0x00000000fede0000 - 0x00000000fedb0000 = 0x20000 = 131072字节


但经过计算 20971520 - 131072 = 20840448仍然有偏差，原因是虚拟机内部没有直接使用from/to的大小，而是进一步做了对齐操作
估算算法如下：


	#defin align_size_down_(size, alignment)  ((size) & ~((alignment) - 1))

	inline intptr_t align_size_down(intptr_t size, intptr_t alignment) {
		return align_size_down_(size, alignment);
	}

	size_t comput_survivor_size(size_t gen_size, size_t alignment) const {
		size_t n = gen_size / (SurvivorRatio + 2);
		return n > alignment ? align_size_down_(n, alignment) : alignment;
	}



上述代码中alignment变量在非ARM平台上为1 << 16，即2<sup>16</sup>，参数gen_size表示新生代的总大小，SurvivorRatio默认值是8，表示幸存代的比例。

故，根据计算，from/to区间大小为：

n = gen_size / (SurvivorRatio + 2) = 6946816/10 = 694681

(size) & ~((alignment) - 1) = 694681 & ~(2<sup>16</sup> - 1) = 0xA9999 & 0xFFFFFFFFFFFE0000 = 0xA0000 = 655360

由20971520 - 655360 = 20316160与实际可用最大堆空间吻合。



## 最大堆和初始堆设置 ##
参数-Xmn可以用于设置新生代的大小。设置一个较大的新生代会减小老年代的大小，这个参数对GC有很大的影响。新生代的大小一般设置为整个堆空间的&frac13;到&frac14;

参数-XX:SurvivorRatio=eden/from=eden/to

在实际工作中，应该根据系统特点做合理的设置，基本策略是：尽可能将对象预留在新生代，减少老年代GC的次数

参数-XX:NewRatio来设置新生代和老年代的比例

堆分配参数示意图如下：

![](https://img-blog.csdnimg.cn/img_convert/65fcbafa76708e5f1f366b4b1082b9a4.png)

# 非堆内存 #
###### 直接内存配置
直接内存的访问速度比堆内存快约40％。若使用-server参数执行，直接内存经过Server的优化，比堆内存访问速度提升将近一个数量级

虽然在访问速度上直接内存有较大优势，但是在内存空间申请时，堆空间的速度远远快于直接内存

由此可见，直接内存适合申请次数少，访问比较频繁的场合；如果需要频繁申请内存空间的场合，并不适合使用直接内存

# String在虚拟机中的实现 #
String对象有三个特性：

1. 不变性

   不变性是指String对象一旦生成，则不能再对它进行任何改变，这个特性可以泛化成不变(immutable)模式，即一个对象的状态在对象被创建后就不能再发生改变。不变模式的主要作用在于当一个对象需要被多线程共享并且访问频繁时，可以省略同步和锁的等待时间，从而大幅提升系统性能。

   类似于String.substring()、String.concat()等看起来像修改的操作，实际上都是靠产生新的字符串实现的，如果要一个可以修改的字符串，则需要StringBuffer或者StringBuilder对象

2. 针对常量池的优化

   指当两个String对象有同一个值的时候，它们只引用常量池的同一个副本，当一个字符串反复出现时，可以大幅度节省内存空间。

3. 类的final定义

   作为final类的String对象在系统中不可能有任何子类，这是对系统安全性的保护。

String对象内存泄漏问题：

String类在JDK1.6之前，调用String.substring时非常容易造成内存泄漏，主要原因是String实际内容由value,offset和count共同决定，而String.substring()调用时，新生成的String并没有从value中获取自己需要的那部分。而是简单地使用了相同的value引用，只是修改了offset和count，以此来确定新的String对象值。当原始字符串没有被回收时，这种情况没有问题，而且节约空间，但是当原始字符串被回收时，就会出现offset和count值很小，但是value有很多的情况，此时value多余的部分就造成了空间浪费。该问题在JDK1.7中被修复，value唯一决定String的实质内容。

String对象常量池位置：

JDK1.6之前，这块区域在永久区。JDK1.7以后，这块区域在堆中。

字面相同的字符串，回收后，再分配会重新加入常量池，但是引用位置已经不同。

# 浅堆和深堆 #
浅堆(Shallow Heap)是指一个对象所消耗的内存，包含了三部分：对象头、实例数据和对齐填充。

1. 对象头：分为两部分。①标记部分，包括 hashcode、gc 分代年龄、锁状态标志、线程持有锁、偏向线程锁id，偏向时间戳，这一部分在32位机器上为 4 byte，64位机器上为 8 byte。②原始对象引用：对象的指针，通过这个指针找到对象的实例，该数据可以压缩，32 位机器上为 4 byte,64位机器上为 8byte，jdk8默认开启压缩，大小为 4 byte。
2. 实例数据：对象实例
3. 对齐填充：补齐8个字节的整数倍

保留集(Retained Set)是指一个只能通过某个对象才能直接或者间接访问到的所有对象的集合。

深堆(Retained Heap)是指保留集中所有对象的浅堆大小之和。

对象实际大小是指对象所能触及的所有对象的浅堆大小之和。

例如：对象A引用对象C和对象D，对象B引用对象C和对象E。那么对象A的浅堆是对象A自身，对象A的深堆是对象A+对象D，对象A的实际大小是对象A+对象C+对象D。

