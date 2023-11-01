/**
 * SingletonIdler class
 * 懒汉式单例模式
 *
 * @author zhanghaoran25
 * @date 2023/10/30 15:25
 */
public class SingletonIdler {
    private static SingletonIdler instance;

    private SingletonIdler() {
    }

    /**
     *  第一次使用单例对象时才创建，判断实例是否已经被创建，如果没有创建则创建于一个实例返回，否则直接返回
     *  线程不安全，多线程场景会创建多个实例
     */
    public static SingletonIdler getInstance() {
        if (instance == null) {
            instance = new SingletonIdler();
        }
        return instance;
    }
}