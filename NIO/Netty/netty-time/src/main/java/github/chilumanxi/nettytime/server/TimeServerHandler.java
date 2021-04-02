package github.chilumanxi.nettytime.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.UnsupportedEncodingException;

public class TimeServerHandler extends ChannelHandlerAdapter {

//    ChannelRead是属于5.X版本的4.X版本没有这个方法，所以如果要用ChannelRead。可以更换5.X版本的Netty。

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("The Time server receive order : " + body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
//        从性能的角度考虑，为了防止频繁地唤醒Selector进行消息发送，Netty的write方法并不直接将消息写入SocketChannel中
//        调用write方法只是将把待发送的消息放到缓冲数组中，再通过调用flush方法，将发送缓冲区中的消息全部写到SocketChannel中
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        ctx.close();
    }


}
