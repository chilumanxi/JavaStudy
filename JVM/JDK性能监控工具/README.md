# JDK性能监控工具 #

## jps ##
查看Java进程，包括其自身

参数：

1. 参数-m可以用于输出传递给Java进程(主函数)的参数
2. 参数-l可以用于输出主函数的完整路径
3. 参数-v可以显示传递给Java虚拟机的参数

示例：

	D:\MyWork\JavaStudy>jps		
	8464 Launcher
	17524 Launcher
	2820 Launcher
	9060 ServerApplication
	12392 Jps

## jstat ##
可以用于观察Java应用程序运行时相关信息的工具，可以通过它查看堆信息的详细情况。

基本语法：

`jstat -<option> [-t] [-h<lines>] <vmid> [<interval> [<count>]]`

参数:
1. option
2. -t参数可以在输出信息前加上时间戳，显示程序运行时间
3. -h参数可以指定在周期性输出数据时，输出多少行以后·输出一个表头信息
4. interval参数用于指定输出统计周期
5. count参数指定输出多少次数据

示例：

	D:\MyWork\JavaStudy>jstat -class -t 9060 1000 2
	Timestamp       Loaded  Bytes  Unloaded  Bytes     Time
    1829.2  18415 34598.8        1     0.9       9.47
    1830.2  18415 34598.8        1     0.9       9.47

-class输出中，Loaded表示载入类数量，第一个Bytes表示载入类合计大小，Unloaded表示卸载类数量，第二个Bytes表示卸载类大小，Time表示在两种类上花费的时间

	D:\MyWork\JavaStudy>jstat -compiler -t 9060
	Timestamp       Compiled Failed Invalid   Time   FailedType FailedMethod
    2356.8    11531      2       0    31.87          1 org/springframework/boot/context/properties/source/SpringConfigurationPropertySources$SourcesIterator fetchNext

-compiler输出中，Compiled表示编译任务执行的次数，Failed表示失败的次数，Invalid表示编译不可用的次数，Time表示编译总耗时，FailedType表示最后一次编译失败的类型，FailedMethod表示最后一次编译失败的类名和方法

	D:\MyWork\JavaStudy>jstat -gc 9060
 	S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT
	30720.0 10752.0  0.0   10496.4 402944.0 135402.1  302080.0   65663.8   104216.0 100351.4 13336.0 12433.8     13    0.163   4      0.363    0.526

-gc输出中，前缀S0代表from区，s1代表to区，E代表eden，O代表old，M代表Meta Space， CCS代表Compressed Class Space，C表示大小，U表示已经使用的大小。YGC表示新生代GC次数，YGCT代表新生代GC花费时间，FGC表示Full GC次数，FGCT表示Full GC时间，GCT表示GC总耗时

	D:\MyWork\JavaStudy>jstat -gccapacity 9060
 	NGCMN    NGCMX     NGC     S0C   S1C       EC      OGCMN      OGCMX       OGC         OC       MCMN     MCMX      MC     CCSMN    CCSMX     CCSC    YGC    FGC
 	87040.0 1389056.0 464384.0 30720.0 10752.0 402944.0   175104.0  2778624.0   302080.0   302080.0      0.0 1140736.0 104216.0      0.0 1048576.0  13336.0     13     4

-gccapacity输出中，NGC代表新生代，OGC代表老年代，MC代表元数据区，CCS代表Compressed Class Space。MN代表最小值，MX代表最大值

	D:\MyWork\JavaStudy>jstat -gccause 9060
  	S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT    LGCC                 GCC
  	0.00  97.62  55.82  21.74  96.29  93.23     13    0.163     4    0.363    0.526 Allocation Failure   No GC

-gccause可以打印最近一次GC和当前GC的原因，LGCC代表上一次GC的原因，GCC代表当前GC的原因

	D:\MyWork\JavaStudy>jstat -gcnew 9060
 	S0C    S1C    S0U    S1U   TT MTT  DSS      EC       EU     YGC     YGCT
	30720.0 10752.0    0.0 10496.4  2  15 30720.0 402944.0 236250.1     13    0.163

-gcnew参数可以查看新生代的一些详细信息。T代表新生代晋升到老年代的年龄，MTT代表新生代晋升到老年代年龄的最大值，DSS代表所需survivor区大小

	D:\MyWork\JavaStudy>jstat -gcnewcapacity 9060
  	NGCMN      NGCMX       NGC      S0CMX     S0C     S1CMX     S1C       ECMX        EC      YGC   FGC
   	87040.0  1389056.0   464384.0 462848.0  30720.0 462848.0  10752.0  1388032.0   402944.0    13     4

-gcnewcapacity参数可以详细输出年轻代各个区的大小信息

	D:\MyWork\JavaStudy>jstat -gcold 9060
  	 MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT
	104216.0 100351.4  13336.0  12433.8    302080.0     65663.8     13     4    0.363    0.526

-gcold用于展现老年代GC的概况

	D:\MyWork\JavaStudy>jstat -gcoldcapacity 9060
   	OGCMN       OGCMX        OGC         OC       YGC   FGC    FGCT     GCT
   	175104.0   2778624.0    302080.0    302080.0    13     4    0.363    0.526
-gcoldcacpacity参数可以输出老年代容量

	D:\MyWork\JavaStudy>jstat -gcutil 9060
  	S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT
  	0.00  97.62  80.20  21.74  96.29  93.23     13    0.163     4    0.363    0.526

-gcutil可以用于展示GC回收相关的信息。

## jinfo ##
语法：

`jinfo [option] <pid>`

可以通过jinfo命令查看虚拟机参数的值，如

	D:\MyWork\JavaStudy>jinfo -flag MaxTenuringThreshold 9060
	-XX:MaxTenuringThreshold=15
查看了新生代晋升到老年代的最大年龄

	D:\MyWork\JavaStudy>jinfo -flag PrintGC 9060
	-XX:-PrintGC
显示没有打印GC

	D:\MyWork\JavaStudy>jinfo -flag +PrintGC 9060

	D:\MyWork\JavaStudy>jinfo -flag PrintGC 9060
	-XX:+PrintGC
jinfo还可以动态修改部分参数的值，如上，动态打开了对GC的打印，但是仅支持部分参数。

## jmap ##

jmap命令是一个多功能的命令，可以生成Java程序的堆Dump文件，也可以查看堆内存实例的统计信息。