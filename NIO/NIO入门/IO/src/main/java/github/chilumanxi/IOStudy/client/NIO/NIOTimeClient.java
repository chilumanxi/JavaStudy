package github.chilumanxi.IOStudy.client.NIO;

public class NIOTimeClient {

    public static void main(String args[]){
        int port = 8082;
        String host = "127.0.0.1";
        if(args != null && args.length >0){
            try {
                port = Integer.valueOf(args[0]);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        new Thread(new NIOTimeClientHandler(host, port), "NIOTimeClient-001").start();
    }
}
