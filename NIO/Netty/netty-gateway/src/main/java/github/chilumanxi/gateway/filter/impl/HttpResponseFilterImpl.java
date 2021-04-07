package github.chilumanxi.gateway.filter.impl;

import github.chilumanxi.gateway.filter.HttpResponseFilter;
import io.netty.handler.codec.http.FullHttpResponse;

public class HttpResponseFilterImpl implements HttpResponseFilter {
    @Override
    public void filter(FullHttpResponse response) {
        response.headers().set("kk", "java-1-nio");
    }
}
