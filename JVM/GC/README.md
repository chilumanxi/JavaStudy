# GC 垃圾回收 #
## 引用计数法(Reference Counting) ##
原理：对于一个对象A，当有任何一个对象引用了A，则A的引用计数器就加1，当引用失效时，A的引用计数器减1，当A的引用计数器为0时则对象A就不可再被使用

问题：
1. 无法处理循环引用问题
2. 引用计数器加1和减1操作会影响性能

## 标记清除法(Mark-Sweep) ##
标记清除法是现代垃圾回收算法的思想基础，分为标记和清除两个阶段

原理：标记阶段通过根节点标记所有从根节点开始的可达对象，因此，未被标记的对象就是未被引用的垃圾对象；清除阶段就清除掉所有未被标记的对象

问题：
可能产生空间碎片，不连续内存空间的工作效率要低于连续内存空间

## 复制算法(Copying) ##
原理：将原有的内存空间分为两块，每次只使用其中的一块。在进行垃圾回收时，将正在使用的内存中的存活对象复制到未使用的内存块中，然后将正在使用的内存清空

优点：如果系统中垃圾对象比较多，复制算法需要复制的存活对象数量就会相对较少，复制效率会比较高，并且能保证新空间的内存连续性

问题：将系统内存折半，消耗大量内存空间。

实例：Java的年轻代使用的就是这样的算法，Eden区和S0把存活对象复制到S1中，然后将原空间清空。如果S0中有老对象和大对象，或者S1空间不足以存放，则直接存放入老年代

## 标记压缩法(Mark-Compact) ##
背景：复制算法的高效是建立在存活对象少，垃圾对象多的前提下的。这种情况多发生在年轻代，但是老年代大部分对象都是存活对象，如果依然复制，成本会很高。

原理：标记压缩算法和标记清除算法一样，首先从根节点开始，对所有可达对象做一次标记，但是之后并不进行清理工作，而是将标记的对象压缩到内存的一端，然后再清理标记以外的所有对象。

标记压缩算法最终效果等同于标记清除执行完后进行了一次内存碎片的整理，故又称为标记清除压缩法

## 分代算法(Generational Collecting) ##
原理：分代算法将内存区间根据对象的特点分为几块，根据每块内存区间的特点使用不同的回收算法，以提高垃圾回收的效率。

为了支持高频率的新生代回收，虚拟机可能使用一种叫做卡表（Card Table）的数据结构。卡表作为一个比特位集合，每一个比特位可以用来表示老年代的某一区域中的所有对象是否持有新生代对象的引用。

这样在新生代GC时，可以不用花大量时间扫描所有的老年代对象来确定每一个对象的引用关系，可以扫描卡表，只有卡表的标记为1时，才需要扫描给定区域的老年代对象，以寻找新生代对象位置。

通过这样的方式，可以大大提升新生代的回收速度。

## 分区算法 (Region) ##
原理：分区算法将整个堆空间划分为连续的不同小区间，每个小区间独立使用，独立回收。

一般来说，相同条件下，堆空间越打，一次GC时间越长，为了更好控制GC的停顿时间(STW)，可以根据回收时间，合理回收若干个小空间，而不是一次性回收整个堆空间，从而减少一次GC所产生的停顿。

优点：可以控制一次回收小区间的数量。


## 串行回收器 ##
### 新生代串行回收器 ###
特点：
1. 仅使用单线程进行垃圾回收
2. 独占式的垃圾回收方式

在串行回收器进行垃圾回收时，Java应用程序中的线程都需要暂停工作，等待垃圾回收完成，这种现象称为"Stop-The-World"，会造成非常糟糕的用户体验，在实时性要求较高的应用场景中，往往是不能接受的。

但是新生代串行回收器使用复制算法，实现相对简单、逻辑处理特别高效且没有线程切换的开销。在诸如单CPU处理器等硬件平台不是特别优越的情况下，它的性能表现可以超过并行回收器和并发回收器。

使用-XX:+UseSerialGC参数可以指定使用新生代串行回收器或老年代串行回收器，当虚拟机在Client模式时，它是默认的垃圾回收器

### 老年代串行回收器 ###
老年代串行回收器也是一个串行的独占式的垃圾回收器。由于老年代垃圾回收通常会需要比新生代垃圾回收更长的时间，在堆空间较大的应用程序中，一旦老年代串行回收器启动，应用程序很可能会因此停顿较长的时间。

使用：
1. -XX:+UseSerialGC 新生代和老年代都使用串行回收器
2. -XX:+UseParNewGC （JDK9、JDK10已经删除，因为ParNew需要和CMS搭配工作，而CMS已经被G1代替，不再支持）：新生代使用ParNew回收器，老年代使用串行回收器
3. -XX:+UseParallelGC 新生代使用ParallelGC回收器，老年代使用串行回收器

## 并行回收器 ##
### 新生代ParNew回收器 ###
ParNew回收器是一个工作在新生代的垃圾回收器，它只是简单地将串行回收器多线程化，它的回收策略、算法及参数和新生代串行回收器相同。

使用：
1. -XX:+UseParNewGC （JDK9、JDK10已经删除，因为ParNew需要和CMS搭配工作，而CMS已经被G1代替，不再支持）：新生代使用ParNew回收器，老年代使用串行回收器
2. -XX:+UseConcMarkSweepGC（JDK9、JDK10不建议使用，建议使用默认的G1）：新生代使用ParNew回收器，老年代使用CMS回收器

ParNew回收器工作时线程数量可以使用-XX:ParallelGCThreads参数指定。一般最好与CPU数量相当，避免过多的线程影响垃圾回收性能。

默认情况下，CPU数量小于8时ParallelGCThreads等于CPU数量，CPU数量大于8时，ParallelGCThreads等于3+((5*CPU_Count)/8)

###新生代ParallelGC回收器 ###
ParallelGC回收器表面上看和ParNew回收器一样，都是使用复制算法，多线程、独占式的回收器。但是ParallelGC特别关注系统的吞吐量。

使用：
1. -XX:+UseParallelGC 新生代使用ParallelGC，老年代使用串行回收器
2. -XX:+UseParallelOldGC 新生代使用ParallelGC，老年代使用ParallelOldGC

ParallelGC回收器提供了两个重要的参数用于控制系统的吞吐量：
1. -XX:MaxGCPauseMillis 设置最大垃圾回收停顿时间，如果刻意降低此参数，会导致虚拟机使用一个较小的堆，导致垃圾回收频繁，增加总时间，降低吞吐量。
2. -XX:GCTimeRatio 设置吞吐量大小（取值0~100整数）。假设GCTimeRatio的值为n，那么系统将花费不超过1/(1+n)的时间进行垃圾回收。比如19代表垃圾回收时间不超过5%，默认值为99，即不超过1%

这两个参数是相反的两个参数，降低一个会增加另外一个

###老年代ParallelOldGC回收器 ###
ParallelOldGC使用标志压缩算法，在JDK1.6中才可以使用

-XX:+UseParallelOldGC 新生代使用ParallelGC，老年代使用ParallelOldGC，对吞吐量敏感的系统中可以考虑使用。

## CMS(Concurrent Mark Sweep)回收器 ##
CMS意为并发标记清除，使用的是标记清除算法，也是一个多线程并行的回收器。与ParallelGC和ParallelOldGC不同，CMS回收器主要关注系统停顿时间。

CMS主要工作步骤：

![](https://img-blog.csdnimg.cn/20210317111043776.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NoaUx1TWFuWGk=,size_16,color_FFFFFF,t_70)

初始标记和重新标记需要独占资源，其他的工作步骤可以和用户线程一起执行

并发标记后，会有一个预清理的操作。预清理是并发的，除了为正式清理做准备和检查，还会尝试控制一次停顿的时间，由于重新标记是独占CPU的，如果新生代GC发生后，立即触发一次重新标记，那么一次停顿的时间可能会很长。

为了避免这种情况，预处理会可以等待一次新生代GC的发生，然后根据历史性能数据预测下一次新生代GC可能发生的时间，在当前时间和预测时间的中间进行重新标记。这样可以尽量避免新生代GC和重新标记重合，尽可能减少一次的停顿时间。

CMS默认启动的并发线程数是(ParallelGCThreads+3)/4，即(GC并行时使用的线程数量+3)/4。并发线程数量也可以通过-XX:ConcGCThreads或者-XX:ParallelCMSThreads来指定。当CPU资源紧张时，受到CMS回收器线程的影响，应用系统的性能在垃圾回收阶段会比较糟糕

由于CMS不是独占式的回收器，在CMS回收过程中，应用程序仍然不停地工作，会不停地产生垃圾，这些垃圾在当前CMS过程中无法被清除，同时因为应用程序没有中断，所以要保证应用程序有足够的内存空间可以使用，所以CMS不会等待堆内存饱和后再进行垃圾回收。

回收的阈值默认是68，即老年代空间使用率到达68%时进行一次CMS回收。如果应用程序内存增长过快，在CMS执行过程中，已经出现了内存不足的情况，CMS回收会失败，转而变成老年代串行回收器进行垃圾回收，完全中断应用程序。

使用参数-XX:CMSInitiatingOccupancyFraction来调节回收的阈值，当内存增长缓慢时，调大以降低CMS的频率。当内存增长快速时，调低以避免频繁触发老年代串行回收器。

-XX:+UseCMSCompactAtFullCollection参数可以用用来使CMS垃圾回收完成后进行一次碎片压缩

-XX:CMSFullGCsBeforeCompaction参数可以用来设置多少次CMS垃圾回收后进行一次碎片压缩

如果希望使用CMS回收Perm区，则必须打开-XX:+CMSClassUnloadingEnabled，条件允许的情况下，会使用CMS的机制回收Perm区

## G1回收器 ##

特点：并行性 并发性 同时兼顾年轻代和老年代 空间整理 可预见性，只回收部分

G1的回收过程分为4个阶段：
1. 新生代GC
2. 并发标记周期
3. 混合回收
4. 如果需要，可能会进行Full GC

### G1的新生代GC ###
一旦eden区被占满，新生代GC就启动，新生代GC只处理eden区和survivor区。回收后eden区被全部清空，但是至少仍存在一个survivor区。老年代区域增多，因为有部分eden区和survivor晋升为老年代。

### G1的并发标记周期 ###
G1的并发阶段与CMS类似，都是为了降低一次停顿的时间，将可以和应用程序并发执行的部分单独提取出来执行。

分为以下几个阶段：
1. 初始标记 标记从根节点直接可达的对象，伴随一次新生代GC
2. 根区域扫描 由于初始标记伴随一次新生代GC，所以在初始标记之后，扫描由survivor区直接可达的老年代区域，并进行标记。这个过程可以和应用程序并发执行。
   但是根区域扫描不能与新生代GC同时进行，当同时发生时，需要先等待标记完成，此举会造成新生代GC时间延长。
3. 并发标记 和CMS类似，扫描并查找存活对象，并做好标记，该过程可与应用程序并发执行，但是会被新生代GC打断。
4. 重新标记 由于在并发标记过程中，应用程序依然在运行。因此标记结果可能需要进行修正。在G1中该过程使用SATB（Snapshot-At-The-Beginning）算法完成。
   即G1会在标记之初为存活对象创建一个快照，这个快照有助于加速重新标记。
5. 独占清理 计算各个区域的存活对象和GC回收的比例，进行排序，会更新记忆集，为并发清理提供要进行清理的信息。
6. 并发清理 识别并清理空闲区域。

并发标记前后最大的不同是经过并发标记多了一些标记为垃圾的区域，是因为该区域中垃圾比例比较高，希望在后续混合GC中进行收集

### 混合回收 ###
并发标记周期会进行部分清理工作，但是清理的比例很小，但是经过并发标记周期，G1回收器已经知道了哪些区域垃圾比较多，就对这些垃圾多的区域按垃圾比例多少选取一些区域进行回收

G1全称为Garbage First，意为垃圾有限，即优先选取垃圾比例比较多的区域

混合回收是因为这个阶段既会执行正常的年轻代GC，又会选取一些被标记的老年代区域进行回收，它同时处理了新生代和老年代。

混合GC会执行很多次，直到回收了足够多的空间，然后它会触发一次新生代GC。从而重复整个G1回收器的过程。

### 必要时的Full GC ###
与CMS类似，并发回收由于让应用程序和回收程序并发运行，所以当老年代空间增长快速时会出现内存不足的情况，此时G1会终止并进行一次Full GC

混合GC时空间不足，或者新生代GC时survivor、老年代无法存储对象时也会进行Full GC

----------

记忆集RS(Remember Set)

如果要回收区域A，可能需要扫描区域B甚至其他所有区域寻找所有对A的引用，记忆集的作用就是记录区域A中的所有引用，到时候直接查找记忆集就可以完成对区域A可达对象。

使用-XX:+UseG1GC标记打开G1的开关

## 对象内存分配和回收的一些细节问题 ##

### System.gc() ###
System.gc()实现如下：

    Runtime.getRuntime().gc()
Runtime.gc()是一个native方法，最终在jvm.cpp实现，如下：


    JVM_ENTRY_NO_ENV(void, JVM_GC(void))
      JVMWrapper("JVM_GC");
      if (!DisableExplicitGC) {
         Universe::heap()->collect(GCCause::_java_lang_system_gc);
      }
    JVM_END
可以看到，如果设置参数-XX:+DisableExplicitGC，条件判断就无法成立，System.gc()就会失效变成空函数

只有打开-XX:+ExplicitGCInvokesConcurrent才能使System.gc()进行并发回收，否则将进行Full GC

### 并行GC前额外触发的新生代GC ###
对于并行回收器的Full GC，在每一次进行之前都会进行一次新生代GC，即实际上System.gc()触发了两次GC

目的在于先将新生代进行一次回收，避免将所有的回收工作同时交给Full GC，尽可能缩短一次停顿时间。

如果不需要此特性，可以使用参数-XX:-ScavengeBeforeFullGC去除发生在Full GC前的新生代GC，默认是进行的。


### 年轻代向老年代晋升 ###
年轻代的对象转存到老年代，这一过程叫做晋升。

晋升年龄是指年轻代经过几轮GC后会自动晋升的轮次。可以通过MaxTenuringThreshold参数来设置这一参数。MaxTenuringThreshold设置的是晋升的必要条件，即最多几轮GC，年轻代的对象必须进行晋升操作。

晋升年龄的计算逻辑：

      size_t desired_survivor_size = (size_t)((((double) survivor_capacity) * TargetSurvivorRatio)/100);
      size_t total = 0;
      int age = 1;
      assert(sizes[0] == 0, "no objects with age zero should be recorded");
      while(age < table_size){
         total + sizes[age];
         if(total > desired_survivor_size) break;
         age ++;
      }
      int result = age < MaxTenuringThreshold ? age : MaxTenuringThreshold;
通锅在上述代码动态的计算了晋升年龄。第1行的desired_survivor_size定义了期望的survivor区的使用大小。然后从小到大对所有年龄段的对象进行统计，当某个年龄age发现survivor区的幸存对象超过了期望值，
那么该age就是晋升年龄。然后从age和设定的MaxTenuringThreshold取较小的一个作为晋升年龄。
确定对象何时晋升的另外一个重要的参数为TargetSurvivorRatio，用于设置survivor区的使用率，即如果survivor区在GC后使用率超过50，那么可能会使一个较小的age作为晋升年龄

### 大对象进入老年代 ###
除了年龄，对象的体积也会影响对象的晋升。假如无论Eden区还是Survivor区都无法容纳该对象的体积，那该对象就会直接进入老年代。

可以通过参数PretenureSizeThreshold来设置对象直接晋升到老年代的阈值，单位是字节。这个参数只对串行回收器和ParNew有效，对ParallelGC无效，默认情况该值为0，即不设阈值。
参考PretenureSizeThreshold.java代码，使用不同参数如下：
1.  `-Xmx32m -Xms32m -XX:+UseSerialGC -XX:+PrintGCDetails` 
    得到结果如下：


    Heap
    def new generation   total 9792K, used 7894K [0x00000000fe000000, 0x00000000feaa0000, 0x00000000feaa0000)
    eden space 8704K,  90% used [0x00000000fe000000, 0x00000000fe7b5bc0, 0x00000000fe880000)
    from space 1088K,   0% used [0x00000000fe880000, 0x00000000fe880000, 0x00000000fe990000)
    to   space 1088K,   0% used [0x00000000fe990000, 0x00000000fe990000, 0x00000000feaa0000)
    tenured generation   total 21888K, used 0K [0x00000000feaa0000, 0x0000000100000000, 0x0000000100000000)
    the space 21888K,   0% used [0x00000000feaa0000, 0x00000000feaa0000, 0x00000000feaa0200, 0x0000000100000000)
    Metaspace       used 3446K, capacity 4496K, committed 4864K, reserved 1056768K
    class space    used 374K, capacity 388K, committed 512K, reserved 1048576K

可以看到，所有的对象均分配在新生代，老年代使用为0，下面我们设置PretenureSizeThreshold参数。

2. `-Xmx32m -Xms32m -XX:+UseSerialGC -XX:+PrintGCDetails -XX:PretenureSizeThreshold=1000`
   得到结果如下：
   

    Heap
    def new generation   total 9792K, used 7878K [0x00000000fe000000, 0x00000000feaa0000, 0x00000000feaa0000)
    eden space 8704K,  90% used [0x00000000fe000000, 0x00000000fe7b1bb0, 0x00000000fe880000)
    from space 1088K,   0% used [0x00000000fe880000, 0x00000000fe880000, 0x00000000fe990000)
    to   space 1088K,   0% used [0x00000000fe990000, 0x00000000fe990000, 0x00000000feaa0000)
    tenured generation   total 21888K, used 16K [0x00000000feaa0000, 0x0000000100000000, 0x0000000100000000)
    the space 21888K,   0% used [0x00000000feaa0000, 0x00000000feaa4010, 0x00000000feaa4200, 0x0000000100000000)
    Metaspace       used 3426K, capacity 4496K, committed 4864K, reserved 1056768K
    class space    used 371K, capacity 388K, committed 512K, reserved 1048576K

我们奇怪的发现，设置以后，按理说应该都分配在老年代，但实际上仍然都分布在了年轻代，不过老年代也有所不同，使用了16k的空间。
出现这种情况的原因是虚拟机在为线程分配空间时，会优先使用一块叫做TLAB的区域，对于体积不大的对象，很有可能会在TLAB上分配，这里把TLAB禁用后继续尝试。

3. `-Xmx32m -Xms32m -XX:+UseSerialGC -XX:+PrintGCDetails -XX:-UseTLAB -XX:PretenureSizeThreshold=1000`
   得到结果如下：
   

    Heap
    def new generation   total 9792K, used 1254K [0x00000000fe000000, 0x00000000feaa0000, 0x00000000feaa0000)
    eden space 8704K,  14% used [0x00000000fe000000, 0x00000000fe139bd8, 0x00000000fe880000)
    from space 1088K,   0% used [0x00000000fe880000, 0x00000000fe880000, 0x00000000fe990000)
    to   space 1088K,   0% used [0x00000000fe990000, 0x00000000fe990000, 0x00000000feaa0000)
    tenured generation   total 21888K, used 6248K [0x00000000feaa0000, 0x0000000100000000, 0x0000000100000000)
    the space 21888K,  28% used [0x00000000feaa0000, 0x00000000ff0ba228, 0x00000000ff0ba400, 0x0000000100000000)
    Metaspace       used 3441K, capacity 4496K, committed 4864K, reserved 1056768K
    class space    used 373K, capacity 388K, committed 512K, reserved 1048576K

可以看到，大量byte数组都已经分配在老年代了

### TLAB ###
TLAB全程为Thread Local Allocation Buffer，即线程本地分配缓存。从名字上可以看到，TLAB是一个线程专用的内存分配区域。

TLAB存在的意义主要是加速对象分配。因为同一时间，可能会有多个线程在堆上申请空间。因此，每一次对象分配都必须进行同步，而在竞争激烈的环境下分配效率会进一步降低。
因此Java虚拟机就使用了TLAB这种线程专属的区域来避免多线程冲突,提高对象分配的效率。TLAB本身占用了eden区的空间，在TLAB启用的情况下，虚拟机会为每一个线程分配一块TLAB区域

参考代码TLABTest.java，TLAB开启与未开启情况下，差了一倍的时间。

由于TLAB空间比较小，所以很容易装满。如一个100KB的TLAB空间，之前已经装了80KB，此时再来一个30KB的对象，就面临选择，是抛弃原有TLAB空间，浪费掉20KB的空间，重新申请新的TLAB空间，
还是将30KB的对象直接分配到堆上，保留原有的TLAB空间，留剩下的20KB到以后用。这取决于一个最大浪费空间(refill_waste)的值。当分配TLAB空间失败时，会判断当前分配的对象是否小于最大浪费空间。
如果当前分配的对象比最大浪费空间小，当前TLAB空间会退回Eden区，重新申请新的TLAB空间将此对象分配；如果当前分配的对象比最大浪费空间大，则虚拟机会将其直接分配在堆上。
默认情况下，TLAB的大小和refill_waste的值会在运行时不断调整。可以使用参数-XX:-ResizeTLAB禁用自动调整并使用参数-XX:TLABSize指定TLAB空间大小。

TLAB是线程私有的，线程初始化的时候，会创建并初始化TLAB。同时，在GC 扫描对象发生之后，线程第一次尝试分配对象的时候，也会创建并初始化TLAB。
TLAB生命周期停止（停止不代表被回收，而是不再被这个线程私有管理）在：
1. 当前TLAB空间不够分配且分配对象的大小小于最大浪费空间，那么这个TLAB将会退回Eden区重新申请。
2. 发生GC时，TLAB被回收。

TLAB浪费的空间由三部分组成，即gc,fast,slow。gc表示发生新生代GC时TLAB空闲的空间，fast和slow都表示TLAB区域被废弃时尚未被使用的空间。

对象分配流程，如果当前流程分配不成功的话就顺序往下进行尝试：

1. 栈上分配
2. TLAB分配
3. 老年代分配
4. Eden区分配
