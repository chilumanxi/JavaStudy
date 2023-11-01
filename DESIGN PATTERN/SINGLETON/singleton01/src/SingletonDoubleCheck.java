/**
 * SingletonDoubleCheck class
 * 双重检验锁单例模式
 *
 * @author zhanghaoran25
 * @date 2023/10/30 15:54
 */
public class SingletonDoubleCheck {
    private static volatile SingletonDoubleCheck instance;

    private SingletonDoubleCheck() {
    }

    /**
     * 通过使用synchronized关键字来确保线程安全性
     */
    public static SingletonDoubleCheck getInstance() {
        if(instance == null){
            synchronized (SingletonDoubleCheck.class){
                if(instance == null){
                    instance = new SingletonDoubleCheck();
                }
            }
        }
        return instance;
    }
}