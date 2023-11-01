import java.util.HashMap;
import java.util.Map;

/**
 * SingletonRegister class
 * 注册式单例模式
 *
 * @author zhanghaoran25
 * @date 2023/10/31 10:47
 */
public class SingletonRegister {
    private static Map<String, SingletonRegister> instances = new HashMap<>();

    private SingletonRegister(){

    }

    /**
     * 在一个静态的Map中保存所有单例对象，然后在需要使用单例对象时通过Map来获取
     */
    public static SingletonRegister getInstance(String name){
        if(!instances.containsKey(name)){
            instances.put(name, new SingletonRegister());
        }
        return instances.get(name);
    }
}