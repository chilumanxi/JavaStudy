package github.chilumanxi.IOStudy.server.AIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServerHandler implements Runnable{
    private int port;
    CountDownLatch latch;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AsyncTimeServerHandler(int port) {
        this.port = port;
        try {

//            创建异步的服务端通道
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
//            绑定监听端口
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("The time Server is start at port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
//        作用是在完成一组正在执行的操作之前，允许当前的线程一直阻塞，防止服务端执行完成后退出
        latch = new CountDownLatch(1);
        doAccept();
        try{
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doAccept() {
//        由于是异步的操作,可以传递一个CompletionHandler<AsynchronousSocketChannel, ? super A>类型的handler实例接受accept操作成功的通知消息
        asynchronousServerSocketChannel.accept(this,
                new AcceptCompletionHandler());
    }
}
