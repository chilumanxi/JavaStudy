package github.chilumanxi.nettyfile.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServerInitializer extends ChannelInitializer<SocketChannel> {
    private int port;
    private String url;
    private String host;

    public HttpFileServerInitializer(int port, String url, String host) {
        this.port = port;
        this.url = url;
        this.host = host;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline ch = socketChannel.pipeline();
//        HTTP请求消息解码器
        ch.addLast("http-decoder", new HttpRequestDecoder());
//        该解码器作用是将多个消息转换为单一的FullHttpRequest或者FullHttpResponse
//        原因是HTTP解码器在每个HTTP消息中会生成多个消息对象
        ch.addLast("http-aggregator", new HttpObjectAggregator(65536));
//        对HTTP响应消息进行解码
        ch.addLast("http-encoder", new HttpResponseEncoder());
//        支持异步发送大的码流（例如大的文件传输），但不占用过多的内存，防止发生Java内存溢出错误
        ch.addLast("http-chunked", new ChunkedWriteHandler());
        ch.addLast("fileServerHandler", new HttpFileServerHandler(url));

    }
}
