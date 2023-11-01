/**
 * SingletonEnum enum
 * 枚举单例模式
 *
 * 利用了Java中枚举类型本身就是单例的特点。枚举单例模式是一种天然线程安全的单例模式实现方式，而且可以防止反射和序列化等攻击。
 *
 * @author zhanghaoran25
 * @date 2023/10/30 19:56
 */
public enum SingletonEnum {
    INSTANCE;
}