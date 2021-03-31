package github.chilumanxi.IOStudy.server.NIO;

import java.io.IOException;

public class NIOTimeServer {
    public static void main(String args[]) throws IOException {
        int port = 8082;
        if(args != null && args.length > 0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){
//                采用默认值
                e.printStackTrace();
            }
        }

        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
    }
}
