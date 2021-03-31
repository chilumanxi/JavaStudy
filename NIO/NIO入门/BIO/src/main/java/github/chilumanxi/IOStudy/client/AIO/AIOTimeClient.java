package github.chilumanxi.IOStudy.client.AIO;

public class AIOTimeClient {
    public static void main(String args[]) {
        int port = 8082;
        String host = "127.0.0.1";
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        new Thread(new AsyncTimeClientHandler(host, port), "AIO-AsyncTimeClientHandler-001").start();
    }
}
