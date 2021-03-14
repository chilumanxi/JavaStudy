# 内存模型 #
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
