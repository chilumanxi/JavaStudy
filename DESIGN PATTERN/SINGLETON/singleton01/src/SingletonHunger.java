/**
 * SingletonHunger class
 * 饿汉式单例模式/静态单例模式
 *
 * @author zhanghaoran25
 * @date 2023/10/30 15:42
 */
public class SingletonHunger {
    private static SingletonHunger instance = new SingletonHunger();

    private SingletonHunger() {

    }

    /**
     * 在类加载的时候就创建实例，在类加载的时候就创建了实例
     * 不存在线程安全问题
     */
    public static SingletonHunger getInstance() {
        return instance;
    }
}