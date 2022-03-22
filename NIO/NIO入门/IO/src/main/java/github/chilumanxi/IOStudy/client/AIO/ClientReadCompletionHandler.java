package github.chilumanxi.IOStudy.client.AIO;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class ClientReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private  AsynchronousSocketChannel channel;
    private CountDownLatch latch;

    public ClientReadCompletionHandler(AsynchronousSocketChannel channel, CountDownLatch latch) {
        this.channel = channel;
        this.latch = latch;
    }


    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] bytes = new byte[attachment.remaining()];
        attachment.get(bytes);
        String body;
        try{
            body = new String(bytes, "UTF-8");
            System.out.println("Now is : " + body);
            latch.countDown();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        exc.printStackTrace();
        try{
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
