import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer01 {
    public static void main(String args[]) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8801);
        while(true){
            try {
                Socket socket = serverSocket.accept();
                service(socket);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private static void service(Socket socket){
        try {
            Thread.sleep(20);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type:text/html;charset=utf-8");
            printWriter.println();
            printWriter.write("hello,nio");
            printWriter.close();
            socket.close();
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}


//BIO 串行执行
//单线程
//sb -u http://127.0.0.1:8801 -c 40 -N 30
//RPS: 47.3 (requests/second)
//Max: 911ms
//Min: 71ms
//Avg: 827.3ms