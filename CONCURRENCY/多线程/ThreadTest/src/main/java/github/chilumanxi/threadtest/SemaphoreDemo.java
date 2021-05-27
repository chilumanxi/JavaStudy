package github.chilumanxi.threadtest;

import java.util.concurrent.*;

public class SemaphoreDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        long start=System.currentTimeMillis();


        Semaphore semaphore = new Semaphore(1);

        FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
//              semaphore.acquire();
                return sum();

            }
        });

        new Thread(task).start();

//        semaphore.release();

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
