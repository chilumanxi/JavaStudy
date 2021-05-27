package github.chilumanxi.threadtest;

import java.util.Random;
import java.util.concurrent.*;

public class CyclicBarrierDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        long start=System.currentTimeMillis();


        CyclicBarrier cyclicBarrier = new CyclicBarrier(1, new Runnable() {
            @Override
            public void run() {
                System.out.println("回调>>"+Thread.currentThread().getName());
                System.out.println("回调>>线程组执行结束");
                System.out.println("==>各个子线程执行结束。。。。");
            }
        });

        FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                cyclicBarrier.await();
                return sum();
            }
        });

        new Thread(task).start();

        System.out.println("异步计算结果为：" + task.get());

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");

        // 然后退出main线程
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}
