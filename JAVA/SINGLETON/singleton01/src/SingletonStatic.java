/**
 * SingletonHolder class
 * 静态内部类单例模式
 *
 * @author zhanghaoran25
 * @date 2023/10/30 19:51
 */
public class SingletonStatic {
    /**
     * 利用了静态内部类只会在被使用时才会加载的特点
     */
    private static class SingletonHolder {
        private static final SingletonStatic INSTANCE = new SingletonStatic();
    }

    private SingletonStatic(){

    }

    public static SingletonStatic getInstance(){
        return SingletonHolder.INSTANCE;
    }
}