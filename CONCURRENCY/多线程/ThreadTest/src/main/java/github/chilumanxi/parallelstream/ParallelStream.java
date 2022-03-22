package github.chilumanxi.parallelstream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class ParallelStream {
    public static void main(String args[]){
        List<Integer> list = new ArrayList<>();
        for(int i = 1; i <= 10000; i ++){
            list.add(i);
        }
        System.out.println("list : " + list.toString());
        BlockingQueue<Long> blockingQueue = new LinkedBlockingQueue<>(10000);
        List<Long> longList = list.stream().parallel()
                .map(i -> i.longValue())
                .sorted()
                .collect(Collectors.toList());
        System.out.println("longList : " + longList.toString());
        longList.stream().parallel().forEach(i ->{
            try{
                blockingQueue.put(i);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        });
        System.out.println("blockingQueue : " + blockingQueue.toString());
    }
}
