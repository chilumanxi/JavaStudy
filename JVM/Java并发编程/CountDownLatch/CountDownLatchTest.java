import java.util.concurrent.CountDownLatch;

public class CountDownLatchTest {

    public static  void main(String args[]) throws InterruptedException {
        CountDownLatch begin = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(2);


        for(int i = 0; i < 2; i ++){
            Thread thread = new Thread(new Player(begin, end));
            thread.start();
        }
        try {
            begin.countDown();
            System.out.println("the race begin!");
            end.await();
            System.out.println("the race end!");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    static class Player implements Runnable{
        private CountDownLatch begin;
        private CountDownLatch end;

        public Player(CountDownLatch begin, CountDownLatch end){
            this.begin = begin;
            this.end = end;
        }

        @Override
        public void run() {
            try{
                begin.await();
                System.out.println(Thread.currentThread().getName() + " arrived !");
                end.countDown();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}


//CountDownLatch是一个同步工具类，允许一个或多个线程等待直到某一个或一组线程工作执行完毕再执行
//本身是一个计数器，使用countDown使计数器减一，当计数器为0时，使用await触发等待的线程