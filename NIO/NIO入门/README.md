# NIO 入门 #

## I/O模型 ##
- 1.阻塞I/O模型
  在进程空间调用recvfrom，其系统调用直到数据包到达且被复制到应用进程的缓冲区中或者发生错误时才返回，在此期间会一直等待。
- 2.非阻塞I/O模型
  recvfrom从应用层到内核的时候，如果该缓冲区没有数据，就直接返回一个EWOULDBLOCK错误，一般对非阻塞I/O模型进行轮询检查这个状态，看内核是不是有数据到来。
- 3.I/O复用模型
  Linux提供select/poll，进程通过将一个或者多个fd传递给select或poll系统调用，阻塞在select操作上，当有fd就绪时，立即调用回调函数rollback
- 4.信号驱动I/O模型
  首先开启套接口信号驱动I/O功能，并通过系统调用sigaction执行一个信号处理函数（此系统调用立即返回，进程继续工作，它是非阻塞的）。当数据准备就绪时，就为该进程生成一个SIGIO信号，通过信号回调通知应用程序调用recvfrom来读取数据，并通知主循环函数处理数据。
- 5.异步I/O
  告知内核启动某个操作，并让内核在整个操作完成后（包括将数据从内核复制到用户自己的缓冲区）通知我们

### I/O多路复用技术 ###
与传统多线程/多进程相比，I/O多路复用的最大优势是系统开销小，系统不需要创建新的额外进程或者线程，也不需要维护这些进程和线程的运行。I/O多路复用的主要应用场景有：

1. 服务器需要同时处理多个处于监听状态或者多个连接状态的套接字
2. 服务器需要同时处理多个网络协议套接字

select一些固有缺陷导致它的应用受到很大限制，所以最终选择了epoll。

select最大的缺陷就是单个进程所打开的fd是有一定限制的，由FD_SETSIZE设置，默认是1024，可以选择修改这个宏后重新编译内核，但是会带来网络效率的下降。epoll就没有这个限制，所支持的FD上限是操作系统的最大文件句柄数。

select/poll另一个致命弱点就是当你拥有一个很大的socket集合，由于网络延时或者链路空闲，任一时刻只有少部分的socket是“活跃”的，select每次调用会线性扫描全部的集合，epoll只会对“活跃”的socket才会主动的去调用callback函数。

无论是select、poll还是epoll都需要把fd消息通知给用户空间，如何避免不必要的内存复制就显得非常重要，epoll通过内核和用户空间mmap同一内存实现。


## BIO ##

### 同步阻塞式I/O ###
BIO的主要问题在于，每当有一个新的客户端请求接入时，服务端必须创建一个新的线程处理新接入的客户端链路，一个线程只能处理一个客户端连接，这种模型显然无法满足高性能、高并发接入的场景。

### 伪异步I/O ###
对线程模型进行优化，后端通过一个线程池来处理多个客户端的请求接入，灵活的调配线程资源，设置线程的最大值，防止由于海量并发接入导致线程耗尽。但由于底层的通信依然采用同步阻塞模型，因此无法从根本上解决问题。

当对Socket的输入流进行读操作的时候，它会一直阻塞下去，直到发生三件事：1.有数据可读 2.可用数据已经读取完毕 3.发生空指针或者是I/O异常。这意味着当对方发送请求或者应答消息比较缓慢、或者网络传输比较慢时，读取输入流一方的通信线程将被长时间阻塞。

## NIO编程 ##

NIO是JDK1.4引入的。NIO弥补了原来同步阻塞I/O的不足，在标准Java代码中提供了高速的、面向块的I/O。

### 相关概念 ###

- 缓冲区 Buffer

Buffer是一个对象，包含一些要写入或者要读出的数据，在面向流的I/O中，可以将数据直接写入或者将数据直接读到Stream对象中。

在NIO库中，所有数据都是用缓冲区处理的。其实质上是一个数组，通常是一个字节数组ByteBuffer，也可以使用其他种类的数组。每一个Buffer类都是Buffer接口的一个子实例。除了ByteBuffer，每一个Buffer类都有完全一样的操作，只是处理的数据类型不一样。因为大多数标准I/O操作都是用ByteBuffer，所以它除了具有一般缓冲区的操作之外还提供一些特有的操作，方便网络读写。

- 通道 Channel

Channel可以通过它读取和写入数据，与流不同，通道是双向的，可以用于读、写或者同时用于读写。

类继承如图：

![](https://img-blog.csdnimg.cn/20210330204859810.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NoaUx1TWFuWGk=,size_16,color_FFFFFF,t_70)

自顶向下看，前三层主要是Channel接口，用于定义它的功能，后面是一些具体的功能类（抽象类），从类图可以看出，实际上Channel可以分为两大类：分别是用于网络读写的SelectableChannel和用于文件操作的FileChannel。

- 多路复用器 Selector

多路复用器提供选择已经就绪的任务的能力。Selector会不断的轮询注册在其上的Channel，如果某个Channel上面有新的TCP连接接入、读和写事件，这个Channel就处于就绪状态，会被Selector轮询出来，然后通过SelectionKey可以获取就绪Channel的集合，进行后续的I/O操作。


### NIO服务端序列图 ###

![](https://img-blog.csdnimg.cn/2021033020544958.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NoaUx1TWFuWGk=,size_16,color_FFFFFF,t_70)

步骤如下：

1. 打开ServerSocketChannel，用于监听客户端的连接，它是所有客户端连接的父管道，代码如下：

   	ServerSocketChannel acceptorSvr = ServerSocketChannel.open();

2. 绑定监听端口，设置连接模式为非阻塞模式，代码如下：

   	acceptorSvr.socket().bind(new InetSocketAddress(InetAddress.getByName("IP"), port));
   	acceptorSvr.configureBlocking(false);

3. 创建Reactor线程，创建多路复用器并启动线程，代码如下：

   	Selector selector = Selector.open();
   	New Thread(new ReactorTask()).start();

4. 将ServerSocketChannel注册到Reactor线程的多路复用器Selector上，监听ACCEPT事件，代码如下：

   	SelectionKey key = acceptorSvr.register(selector, SelectionKey.OP_ACCEPT, ioHandler);

5. 多路复用器在线程run方法的无限循环体轮询准备就绪的Key，代码如下：

   	int num = selector.select();
   	Set selectedKeys = selector.selectedKeys();
   	Iterator it = selectedKeys.iterator();
   	while(it.hasNext()){
   		SelectionKey key = (SelectionKey)it.next();
   		// ... deal with I/O event ...
   	}

6. 多路复用器监听到有新的客户端接入，处理新的接入请求，完成TCP三次握手，建立物理链路，代码如下：

   	SocketChannel channel = svrChannel.accept();

7. 设置客户端链路为非阻塞模式，代码如下：

   	channel.configureBlocking(false);
   	channel.socket().setReuseAddress(true);

8. 将新接入的客户端连接注册到Reactor线程的多路复用器上，监听读操作，用来读取客户端发送的网络消息，代码如下：

   	SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ, ioHandler); 

9. 异步读取客户端请求消息到缓冲区，代码如下：

   	int readNumber = channel.read(receivedBuffer);

10. 对ByteBuffer进行编解码，如果有半包消息至臻reset，继续读取后续的保温，讲解码成功的消息封装成Task，投递到业务线程池中，进行业务逻辑编排，代码如下：

    	Object message = null;
    	while(buffer.hasRemain()){
    		byteBuffer.mark();
    		Object message = decode(byteBuffer);
    		if(message == null){
    			byteBuffer.reset();
    			break;
    		}
    		messageList.add(message);
    	}
    	if(!byteBuffer.hasRemain()){
    		byteBuffer.clear();
    	}else{
    		byteBuffer.compact();
    	}	
    	if(messageList != null & !messageList.isEmpty()){
    		for(Object messagE : messageList){
    			handlerTask(messagE);
    		}
    	}

11. 将POJO对象encode成ByteBuffer，调用SocketChannel的异步write接口，将消息异步发送给客户端，代码如下：

    	socketChannel.write(buffer);