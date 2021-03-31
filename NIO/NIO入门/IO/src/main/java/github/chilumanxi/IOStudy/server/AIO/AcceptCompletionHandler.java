package github.chilumanxi.IOStudy.server.AIO;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {
    @Override
    public void completed(AsynchronousSocketChannel channel, AsyncTimeServerHandler attachment) {
//        从attachment获取成员变量asynchronousServerSocketChannel继续调用accept方法。
//        再次调用accept方法的原因是调用一次以后，如果有新的客户端连接接入，系统将回调我们传入的CompletionHandler实例的completed方法，表示新的客户端已经连接成功
//        因为一个asynchronousServerSocketChannel可以接受成千上万个客户端，所以我们需要继续调用它的accept方法，接收其他的客户端连接，最终形成一个循环。
        attachment.asynchronousServerSocketChannel.accept(attachment, this);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        使用read方法进行异步读操作，参数如下：
//        ByteBuffer dst     接收缓冲区，用于从异步Channel中读取数据包
//        A attachment       异步Channel携带的附件，通知回调的时候作为入参使用
//        CompletionHandler<Integer, ? super A> handler    接受通知回调的业务handler
        channel.read(buffer, buffer, new ReadCompletionHandler(channel));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        exc.printStackTrace();
        attachment.latch.countDown();
    }

}
