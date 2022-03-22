package github.chilumanxi.IOStudy.server.AIO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class WriteCompletionHandler implements CompletionHandler<Integer, ByteBuffer>{

    private AsynchronousSocketChannel channel;

    public WriteCompletionHandler(AsynchronousSocketChannel channel) {
        if(this.channel == null)
            this.channel = channel;
    }

    @Override
        public void completed (Integer result, ByteBuffer buffer){
//        对发送的writeBuffer进行判断，如果还有剩余的字节科协，说明没有发送完成，需要继续发送，则继续回调
        if (buffer.hasRemaining()) {
            channel.write(buffer, buffer, this);
        }
    }

        @Override
        public void failed (Throwable exc, ByteBuffer attachment){
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}