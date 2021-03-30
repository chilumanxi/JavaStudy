package github.chilumanxi.BIOTest.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer {
    public static void main(String args[]) throws IOException {
        int port = 8081;
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

//        ServerSocket server = null;
//        try{
//            server = new ServerSocket(port);
//            System.out.println("The time server is start in port : " + port);
//            Socket socket = null;

//            伪异步I/O
//            TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50, 1000);
//            while(true){
//                socket = server.accept();
//                singleExecutor.execute(new TimeServerHanler(socket));
//            }


//            while(true){
//                socket = server.accept();
//                new Thread(new TimeServerHanler(socket)).start();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            if(server != null){
//                System.out.println("The time Server close");
//                server.close();
//                server = null;
//            }
//        }
    }
}

