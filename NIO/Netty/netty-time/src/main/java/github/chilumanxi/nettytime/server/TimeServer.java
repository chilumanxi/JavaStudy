package github.chilumanxi.nettytime.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Properties;

public class TimeServer {
    public static void main(String args[]){
        int port = 8084;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        Properties prop = System.getProperties();
        prop.setProperty("line.separator", "\n");

        new TimeServer().bind(port);
    }

    private void bind(int port) {
//        线程组，包含了一组NIO线程，专门用于网络事件的处理，其实是Reactor线程组
//        NioEventLoopGroup 是用来处理I/O操作的多线程事件循环器

//        bossGroup负责服务端接受客户端的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
//        workGroup负责进行SocketChannel的网络读写
        EventLoopGroup workGroup = new NioEventLoopGroup();
//
        try{
//            Netty用于启动NIO服务端的辅助启动类
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)          //功能对应于JDK NIO类库中的ServerSocketChannel类
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new TimeServerInitializer());       //作用类似于Reactor的handler类，主要用于处理网络I/O事件，如记录日志，对消息进行编解码等

//            绑定端口，调用同步阻塞方法sync等待绑定操作完成。完成之后Netty会返回一个ChannelFuture，主要用于异步操作的通知回调
            ChannelFuture f = b.bind(port).sync();
            System.out.println("The time Server is start at port : " + port);
//            等待服务器监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
//            释放线程池资源
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}
