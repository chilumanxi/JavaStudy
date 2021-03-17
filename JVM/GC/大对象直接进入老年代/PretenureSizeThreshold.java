import java.util.HashMap;
import java.util.Map;

public class PretenureSizeThreshold {
    public static final int _1k = 1024;
    /*
    该程序分配了约5MB空间，大约为5K多个byte数组，每个1K大小
    通过不同的参数观察GC和堆情况
     */

    public static void main(String args[]){
        Map<Integer, byte[]> map = new HashMap<Integer, byte[]>();
        for(int i = 0; i < 5 * _1k; i ++){
            byte[] b = new byte[_1k];
            map.put(i, b);
        }
    }
}
