import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class Atomic {
    private static final int MAX_THREADS = 3;
    private static final int TASK_COUNT = 3;
    private static final int TARGET_COUNT = 10000000;

    private AtomicLong account = new AtomicLong(0L);
    private long count = 0;
    private LongAdder laccount = new LongAdder();

    static CountDownLatch cdlSync = new CountDownLatch(TASK_COUNT);
    static CountDownLatch cdlAtomic = new CountDownLatch(TASK_COUNT);
    static CountDownLatch cdlLongAdder = new CountDownLatch(TASK_COUNT);

    protected synchronized long inc(){
        return ++count;
    }

    protected synchronized long getCount(){
        return count;
    }

    private void clearCount(){
        count = 0;
    }

    public class SyncThread implements Runnable{
        protected String name;
        protected long startTime;
        Atomic out;

        public SyncThread(Atomic o, long st){
            out = o;
            this.startTime = st;
        }

        @Override
        public void run() {
            long v = out.getCount();
            while(v < TARGET_COUNT){
                v = out.inc();
            }
            long endTime = System.currentTimeMillis();
            System.out.println("SyncThread spend:" + (endTime - startTime) + "ms" + " v=" + v);
            cdlSync.countDown();
        }
    }

    public void testSync() throws InterruptedException{
        ExecutorService exe = Executors.newFixedThreadPool(MAX_THREADS);
        long startTime = System.currentTimeMillis();
        SyncThread sync = new SyncThread(this, startTime);
        for(int i = 0; i < TASK_COUNT; i ++){
            exe.submit(sync);
        }
        cdlSync.await();
        exe.shutdown();
    }

    public class AtomicThread implements Runnable{
        protected String name;
        protected long startTime;

        public AtomicThread(long st){
            this.startTime = st;
        }

        @Override
        public void run() {
            long v = account.get();
            while(v < TARGET_COUNT){
                v = account.incrementAndGet();
            }
            long endTime = System.currentTimeMillis();
            System.out.println("AtomicThread spend:" + (endTime - startTime) + "ms" + " v=" + v);
            cdlAtomic.countDown();
        }
    }

    public void testAtomic() throws InterruptedException{
        ExecutorService exe = Executors.newFixedThreadPool(MAX_THREADS);
        long startTime = System.currentTimeMillis();
        AtomicThread atomic = new AtomicThread(startTime);
        for(int i = 0; i < TASK_COUNT; i ++){
            exe.submit(atomic);
        }
        cdlAtomic.await();
        exe.shutdown();
    }

    public class LongAdderThread implements Runnable{
        protected String name;
        protected long startTime;

        public LongAdderThread(long st){
            this.startTime = st;
        }

        @Override
        public void run() {
            long v = laccount.sum();
            while(v < TARGET_COUNT){
                laccount.increment();
                v = laccount.sum();
            }
            long endTime = System.currentTimeMillis();
            System.out.println("LongAdderThread spend:" + (endTime - startTime) + "ms" + " v=" + v);
            cdlLongAdder.countDown();
        }
    }

    public void testLongAdder() throws InterruptedException{
        ExecutorService exe = Executors.newFixedThreadPool(MAX_THREADS);
        long startTime = System.currentTimeMillis();
        LongAdderThread longAdderThread = new LongAdderThread(startTime);
        for(int i = 0; i < TASK_COUNT; i ++){
            exe.submit(longAdderThread);
        }
        cdlLongAdder.await();
        exe.shutdown();
    }

    public static void main(String args[]) throws InterruptedException{
        Atomic a = new Atomic();
        a.testSync();
        a.testAtomic();
        a.testAtomic();
    }
}

//        SyncThread spend:406ms v=10000002
//        SyncThread spend:406ms v=10000001
//        SyncThread spend:406ms v=10000000
//        AtomicThread spend:166ms v=10000002
//        AtomicThread spend:166ms v=10000000
//        AtomicThread spend:166ms v=10000001
//        AtomicThread spend:0ms v=10000002
//        AtomicThread spend:0ms v=10000002
//        AtomicThread spend:1ms v=10000002