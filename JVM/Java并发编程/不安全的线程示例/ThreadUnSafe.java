import java.util.ArrayList;
import java.util.List;

public class ThreadUnSafe {
    public static List<Integer> numberList = new ArrayList<Integer>();
    public static class AddToList implements Runnable{
        int startNum = 0;
        public AddToList(int startNumber){
            startNum = startNumber;
        }

        @Override
        public void run(){
            int count = 0;
            while(count < 1e6){
                numberList.add(startNum);
                startNum += 2;
                count ++;
            }
        }
    }

    public static  void main(String args[]){
        Thread t1 = new Thread(new AddToList(0));
        Thread t2 = new Thread(new AddToList(1));

//        t1.run();
//        t2.run();

        t1.start();
        t2.start();
    }
}

//运行结果：
//    Exception in thread "Thread-0" java.lang.ArrayIndexOutOfBoundsException: 10
//        at java.util.ArrayList.add(ArrayList.java:463)
//        at ThreadUnSafe$AddToList.run(ThreadUnSafe.java:16)
//        at java.lang.Thread.run(Thread.java:748)
//    两个线程同时写ArrayList，破坏了ArrayList的内部一致性，用Vector