## Netty ##
## Netty和Java原生NIO ##
不选择直接使用JDK的NIO类库进行开发，具体原因在于NIO的类库和API比较繁杂、而且需要Java多线程编程知识，可靠性能力补齐的工作量和工作难度都很大，并且自身有轮询bug，如epoll bug，会导致cpu 100%。

Netty作为也就最流行的NIO框架，优点有：API使用简单、功能强大，内置多种编解码功能、性能高、可扩展、成熟稳定、社区活跃。


Netty实现具体详见netty-time

## TCP粘包/拆包 ##

TCP是个“流”协议，底层并不了解上层业务数据的具体意义，所以会根据TCP缓冲区的实际情况进行包的划分，就可能会出现一个完整的包被TCP分成多个包进行发送，也有可能把多个小的包封装成一个大的数据包发送，这就是TCP粘包/拆包问题。

粘包是指服务端一次性收到了两个数据包D1和D2两个粘合在了一起。

拆包是指服务端分两次读取到了两个数据包，第一次读取到了完整的D1包和D2包的部分内容，第二次读取到了D2包剩下的内容。

### 导致粘包/拆包的原因 ###

1. 应用程序write写入的字节大小大于套接口发送缓冲区大小
2. 进行MSS（最大分段大小）大小的TCP分段
3. 以太网帧的payload大于MTU进行IP分片

### 粘包/拆包的解决方案 ###
1. 消息定长，每个报文固定大小，如果不够空位补空格
2. 在包尾增加回车换行符进行分割，如FTP协议
3. 将消息分为消息头和消息体，消息头中包含表示消息总长度的字段
4. 更复杂的应用层协议

具体粘包实例如StickyPacketTimeServerHandler.java和StickyPacketTimeClientHandler.java

具体粘包解决实例如SolveStickyPacketTimeServerHandler.java和StickyPacketTimeClientHandler.java

### LineBasedFrameDecoder ###

LineBasedFrameDecoder的工作原理是它依次遍历ByteBuf中的可读字节，判断是否有"\n"或者"\r\n"，如果有，就以此位置为结束位置，从可读索引到结束位置区间的字节就组成了一行。它是以换行符为结束标志的解码器，支持配置单行的最大长度。如果连续读到最大长度后仍然没有发现换行符，就会抛出异常，同时忽略之前读到的异常码流

### StringDecoder ###
将接收到的对象转化成字符串，然后继续调用后面的handler。与LineBasedFrameDecoder组合就是安航切换的文本解码器。

### DelimiterBasedFrameDecoder ###
按照指定特殊分隔符进行自动解码

### FixedLengthDecoder ###
按照固定长度进行自动解码



## Java 编解码 ##
Java序列化的目的主要有：1、用于网络传输 2、用于对象持久化

客户端需要把要传输的Java对象编码为字节数组或者ByteBuffer对象，服务端读取到ByteBuffer对象或者字节数组时，需要将其解码为Java对象。

Java序列化主要缺点有：

1. 无法跨语言，序列化后的字节数组无法被其他语言解码，RPC框架几乎都没有使用
2. 序列化后的码流太大，并且Java原生序列化的性能很差

因此通常不会选择Java序列化作为远程跨节点调用的编解码框架

### Google的Protobuf ###
全称为Google Protocol Buffers，将数据结构以.proto文件进行描述，通过代码生成工具可以生成对应数据结构的POJO对象和Protobuf相关的方法和属性

特点有：

1. 结构化数据存储格式（XML，JSON等）
2. 搞笑的编解码性能
3. 语言无关，平台无关，扩展性好
4. 官方支持Java，C++和Python

不使用XML的原因是XML解析的时间开销和XML为了可读性而牺牲的空间开销都非常大，因此不适合做高性能的通信协议。Protobuf使用二进制编码，在空间和性能上具有更大的优势

Protobuf的编解码性能远高于其他几种序列化和反序列化。也是RPC框架选用Protobuf做编解码框架的原因

### FaceBook的Thrift ###
Thrift是为了解决Facebook各系统间大数据量的传输通信以及系统之间语言环境不同需要跨平台的特性，因此Thrift可以支持多种程序语言。

在多种不同语言之间通信，Thrift可以作为高性能的通信中间件使用，适用于静态的数据交换。


### JBoss Marshalling ###
是一个Java对象的序列化API包，修正了JDK自带的序列化包的很多问题，但又保持跟java.io.Serializable接口兼容，同事增加了一些可调的参数和附加的特性，并且这些参数和特性可通过工厂类进行配置。

相较于前两种，JBoss Marshalling使用非常简单，更多的在JBoss内部使用，应用范围有限。