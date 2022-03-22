package github.chilumanxi.nettytime.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline ch =  socketChannel.pipeline();

        ch.addLast(new StringDecoder());
        ch.addLast(new TimeClientHandler());

//        粘包测试：
//        ch.addLast(new StickyPacketTimeClientHandler());

//        解决粘包问题
//        ch.addLast(new LineBasedFrameDecoder(1024));
//        ch.addLast(new StringDecoder());
//        ch.addLast(new SolveStickyPacketTimeClientHandler());
    }

}
