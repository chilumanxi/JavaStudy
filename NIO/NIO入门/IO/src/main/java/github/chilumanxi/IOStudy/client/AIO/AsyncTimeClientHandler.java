package github.chilumanxi.IOStudy.client.AIO;

import github.chilumanxi.IOStudy.server.AIO.WriteCompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeClientHandler implements CompletionHandler<Void, AsyncTimeClientHandler>, Runnable {

    private String host;
    private int port;
    private CountDownLatch latch;
    private AsynchronousSocketChannel client;

    public AsyncTimeClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
        try{
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
//        connect方法有三个参数：
//        SocketAddress remote   连接的服务器端口
//        A attachment,          AsynchronousSocketChannel的附件，用于回调通知时作为入参被传递
//        CompletionHandler<Void, ? super A> handler)   异步操作回调通知接口，由调用者实现
        client.connect(new InetSocketAddress(host, port), this, this);
        try{
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try{
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void completed(Void result, AsyncTimeClientHandler attachment) {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        client.write(writeBuffer, writeBuffer, new ClientWriteCompletionHandler(client, latch));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
        exc.printStackTrace();
        attachment.latch.countDown();
    }
}
