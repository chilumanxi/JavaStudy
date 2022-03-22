package github.chilumanxi.IOStudy.client.AIO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class ClientWriteCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel channel;
    private CountDownLatch latch;

    public ClientWriteCompletionHandler(AsynchronousSocketChannel channel, CountDownLatch latch){
        this.channel = channel;
        this.latch = latch;
    }


    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        if(attachment.hasRemaining()){
            channel.write(attachment, attachment, this);
        }else{
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            channel.read(readBuffer, readBuffer, new ClientReadCompletionHandler(channel, latch));
        }

    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        exc.printStackTrace();
        try {
            this.channel.close();
            this.latch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
