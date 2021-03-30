package github.chilumanxi.gateway.outbound;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.*;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpOutboundHandler {
    private static CloseableHttpAsyncClient httpClient;
    private ExecutorService proxyService;
    private String backendUrl;

    public HttpOutboundHandler(String backendUrl) {
        this.backendUrl = backendUrl.endsWith("/") ? backendUrl.substring(0, backendUrl.length() - 1) : backendUrl;
        int cores = Runtime.getRuntime().availableProcessors() * 2;
        long keepAliveTime = 1000;
        int queueSize = 2048;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();

        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("proxyService"), handler);

        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setConnectTimeout(1000)
                .setSoTimeout(1000)
                .setIoThreadCount(cores)
                .setRcvBufSize(32 * 1024)
                .build();

        httpClient = HttpAsyncClients.custom().setMaxConnTotal(40)
                .setMaxConnPerRoute(8)
                .setDefaultIOReactorConfig(ioReactorConfig)
                .setKeepAliveStrategy((httpResponse, httpContext) -> 6000)
                .build();

        httpClient.start();
    }

    public void handler(FullHttpRequest fullHttpRequest, final ChannelHandlerContext ctx) {
        final String url = this.backendUrl + fullHttpRequest.uri();
        proxyService.submit(() -> fetchGet(fullHttpRequest, ctx, url));
    }

    private void fetchGet(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx, String url) {
        final HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        httpGet.setHeader("nio", fullHttpRequest.headers().get("nio"));
        httpClient.execute(httpGet, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse httpResponse) {
                try{
                    handleResponse(fullHttpRequest, ctx, httpResponse);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Exception e) {
                httpGet.abort();
                e.printStackTrace();
            }

            @Override
            public void cancelled() {
                httpGet.abort();
            }
        });
    }

    private void handleResponse(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx, HttpResponse httpResponse) {
        FullHttpResponse fullHttpResponse = null;
        try{
            byte[] body = EntityUtils.toByteArray(httpResponse.getEntity());

            fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));
            fullHttpResponse.headers().set("nio", fullHttpRequest.headers().get("nio"));
            fullHttpResponse.headers().set("Content-Type", "application/json");
            fullHttpResponse.headers().setInt("Content-Length", Integer.parseInt(httpResponse.getFirstHeader("Content-Length").getValue()));
        } catch (IOException e) {
            e.printStackTrace();
            fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            e.printStackTrace();
            ctx.close();
        }finally {
            if(fullHttpRequest != null){
                if(!HttpUtil.isKeepAlive(fullHttpRequest)){
                    ctx.write(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
                }else{
                    ctx.write(fullHttpResponse);
                }
            }
            ctx.flush();
        }

    }
}
