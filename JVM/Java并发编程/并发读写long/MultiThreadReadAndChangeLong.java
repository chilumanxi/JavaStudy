public class MultiThreadReadAndChangeLong {
    public static volatile long t = 0;
    public static class changeT implements Runnable{
        private long to;
        public changeT(long to){
            this.to = to;
        }

        @Override
        public void run() {
            while(true){
                MultiThreadReadAndChangeLong.t = to;
                Thread.yield();
            }
        }
    }

    public static class readT implements Runnable{
        @Override
        public void run() {
            while(true) {
                long rd = MultiThreadReadAndChangeLong.t;
                if (rd != -111L && rd != -999L && rd != 333L && rd != -444L) {
                    System.out.println(rd);
                }
                Thread.yield();
            }
        }
    }

    public static void main(String args[]){
        new Thread(new changeT(-111L)).start();
        new Thread(new changeT(-999L)).start();
        new Thread(new changeT(333L)).start();
        new Thread(new changeT(-444L)).start();
        new Thread(new readT());
    }

}



//该程序在32位机器上运行时将会打印错误rd值
//原因是32位机器一次只能写32位，可能会出现一个线程写了32位，另外一个线程写了32位的情况，导致错误
//可将 public static long t = 0 改为 public static volatile long t = 0