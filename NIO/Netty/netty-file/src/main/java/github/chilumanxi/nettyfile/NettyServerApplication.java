package github.chilumanxi.nettyfile;

import github.chilumanxi.nettyfile.server.HttpFileServer;

public class NettyServerApplication {
    public final static String DEFAULT_URL = "/src/com/phei/netty";
    public final static int port = 8080;

    public static  void main(String args[]){
        String proxyPort = System.getProperty("proxyPort", "8080");
        String proxyUrl = System.getProperty("proxyUrl", "/src/main/java/github/chilumanxi/nettyfile");
        String proxyHost = System.getProperty("proxyHost", "127.0.0.1");

        int port = Integer.parseInt(proxyPort);
        HttpFileServer server = new HttpFileServer(port, proxyUrl, proxyHost);
        System.out.println("The server is starting at port : " + proxyPort);

        try{
            server.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
