import static java.lang.Thread.sleep;

public class visibleTest {
    public static class MyThread extends Thread{
        private boolean stop = false;
        public void stopMe(){
            this.stop = true;
        }

        @Override
        public void run() {
            int i = 0;
            while(!this.stop){
                i ++;
            }
            System.out.println("The Thread has Stop");
        }
    }

    public static void main(String args[]) throws InterruptedException {
        MyThread myThread = new MyThread();
        myThread.start();
        myThread.sleep(1000);
        myThread.stopMe();
        myThread.sleep(1000);

    }
}

//程序不会结束，因为由于系统编译器的优化，部分变量值可能会被寄存器或者高速缓冲缓存，而每个CPU拥有独立的寄存器和cache，从而导致线程之前无法立即发现这个修改。
//对应在程序中就是主线程对stop的修改无法反映到myThread线程中去
//解决办法是给stop加上volatile,或者是给方法加上synchronized