package github.chilumanxi.subscribe.client;

import github.chilumanxi.subscribe.req.SubscribeReq;
import github.chilumanxi.subscribe.req.SubscribeResp;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqClientHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        for(int i = 0; i < 10; i ++){
            ctx.write(subReq(i));
        }
        ctx.flush();
    }

    private SubscribeReq subReq(int cc) {
        SubscribeReq req = new SubscribeReq();
        req.setAddress("北京市通州区测试地址");
        req.setPhoneNumber("12345678");
        req.setProductName("Netty Book");
        req.setSubReqID(cc);
        req.setUserName("chilumanxi");
        return req;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        System.out.println("Receive server response : [" + msg + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

}
