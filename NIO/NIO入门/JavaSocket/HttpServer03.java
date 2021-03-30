import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer03 {
    public static void main(String args[]) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(40);
        ServerSocket serverSocket = new ServerSocket(8803);
        while(true){
            try {
                Socket socket = serverSocket.accept();
                executorService.execute(() -> service(socket));
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


//
//提前准备40个线程的线程池，减少上下文切换的开销
//sb -u http://127.0.0.1:8802 -c 40 -N 30
//RPS: 1605.1 (requests/second)
//Max: 141ms
//Min: 19ms
//Avg: 21.8ms

