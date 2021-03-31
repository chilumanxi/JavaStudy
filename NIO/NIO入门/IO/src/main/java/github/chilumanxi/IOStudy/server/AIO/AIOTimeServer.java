package github.chilumanxi.IOStudy.server.AIO;

public class AIOTimeServer {
    public static void main(String args[]) {
        int port = 8082;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
//                采用默认值
                e.printStackTrace();
            }
        }
//            异步服务器处理类
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
        new Thread(timeServer, "AIO-AsyncTimeServerHandler-001").start();
    }
}
