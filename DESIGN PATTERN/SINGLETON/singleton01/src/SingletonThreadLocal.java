/**
 * SingletonThreadLocal class
 * ThreadLocal单例模式
 *
 * @author zhanghaoran25
 * @date 2023/10/30 19:57
 */
public class SingletonThreadLocal {
    /**
     * 在ThreadLocal中保存单例对象，每个线程都有自己的ThreadLocal副本，从而避免了线程安全性问题。
     */
    private static final ThreadLocal<SingletonThreadLocal> INSTANCE = new ThreadLocal<SingletonThreadLocal>(){
        @Override
        protected SingletonThreadLocal initialValue() {
            return new SingletonThreadLocal();
        }
    };

    private SingletonThreadLocal(){

    }

    public static SingletonThreadLocal getInstance() {
        return INSTANCE.get();
    }
}