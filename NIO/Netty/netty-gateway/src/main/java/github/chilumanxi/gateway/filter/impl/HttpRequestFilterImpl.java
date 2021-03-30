package github.chilumanxi.gateway.filter.impl;

import github.chilumanxi.gateway.filter.HttpRequestFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class HttpRequestFilterImpl implements HttpRequestFilter {

    @Override
    public void filter(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        filterHead(fullHttpRequest,ctx);
    }

    private void filterHead(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        fullHttpRequest.headers().set("nio","ChiLuManXi");
    }
}
