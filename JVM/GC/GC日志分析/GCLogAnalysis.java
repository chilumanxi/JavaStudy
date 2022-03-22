import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class GCLogAnalysis {
    private static Random random = new Random();
    public static void main(String args[]){
        long startMillis = System.currentTimeMillis();
        long duringMillis = TimeUnit.SECONDS.toMillis(1);
        long endMillis = startMillis + duringMillis;

        LongAdder counter = new LongAdder();
        System.out.println("正在执行...");

        int cacheSize = 2000;
        Object[] cacheGarbage = new Object[cacheSize];

        while(System.currentTimeMillis() < endMillis){
            Object garbage = generateGarbage(100 * 1024);
            counter.increment();
            int randomIndex = random.nextInt(2 * cacheSize);
            if(randomIndex < cacheSize){
                cacheGarbage[randomIndex] = garbage;
            }
        }
        System.out.println("执行结束，共生成对象次数：" + counter.longValue());
    }

    private static Object generateGarbage(int max){
        int randomSize = random.nextInt(max);
        int type = randomSize % 4;
        Object result = null;
        switch (type){
            case 0:
                result = new int[randomSize];
                break;
            case 1:
                result = new byte[randomSize];
                break;
            case 2:
                result = new double[randomSize];
                break;
            default:
                StringBuilder builder = new StringBuilder();
                String randomString = "randomString-Anything";
                while(builder.length() < randomSize){
                    builder.append(randomString);
                    builder.append(max);
                    builder.append(randomSize);
                }
                result = builder.toString();
                break;
        }
        return result;
    }
}


//java -Xloggc:gc.demo.log -XX:+PrintGCDetails GCLogAnalysis可以输出打印的GC日志文件