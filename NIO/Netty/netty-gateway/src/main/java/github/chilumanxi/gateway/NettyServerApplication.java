package github.chilumanxi.gateway;

import github.chilumanxi.gateway.inbound.HttpInboundServer;

public class NettyServerApplication {
    public final static String GATEWAY_NAME = "NIOGateway";
    public final static String GATEWAY_VERSION = "1.0.0";

    public static void main(String args[]){
        String proxyServer = System.getProperty("proxyServer", "http://127.0.0.1:8088");
        String proxyPort = System.getProperty("proxyPort", "8888");


        int port = Integer.parseInt(proxyPort);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION + " starting...");
        HttpInboundServer server = new HttpInboundServer(port, proxyServer);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" started at http://localhost:" + port + " for server:" + proxyServer);
        try{
            server.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
