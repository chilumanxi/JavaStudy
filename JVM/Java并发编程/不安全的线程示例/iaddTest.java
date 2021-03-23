public class iaddTest {
    public static volatile int i = 0;

    public static class iadd implements Runnable{

        @Override
        public void run() {
            for(int j = 0; j < 1e5; j ++) i ++;
        }
    }

    public static class isub implements Runnable{

        @Override
        public void run() {
            for(int j = 0; j < 1e5; j ++) i --;
        }
    }



    public static void main(String args[]) throws InterruptedException {
        Thread thread1 = new Thread(new iadd());
        Thread thread2 = new Thread(new isub());

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println(i);

    }

}


//每次运行结束得到的结果不一样
//volatile能解决“内存可见性”引起的问题，但是解决不了操作过程引起的问题。