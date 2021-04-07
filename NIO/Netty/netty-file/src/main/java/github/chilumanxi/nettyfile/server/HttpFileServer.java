package github.chilumanxi.nettyfile.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpFileServer {
    private int port;
    private String url;
    private String host;

    public HttpFileServer(int port, String url, String host) {
        this.port = port;
        this.url = url;
        this.host = host;
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new HttpFileServerInitializer(port, url, host));
            ChannelFuture f = b.bind(host, port).sync();
            System.out.println("HTTP 文件服务器启动...");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
