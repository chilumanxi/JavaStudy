package github.chilumanxi.threadlocal;

import lombok.Data;
import org.springframework.core.NamedThreadLocal;

/**
 * ThreadObj class
 *
 * @author zhanghaoran25
 * @date 2022/3/31 15:31
 */
public class ThreadObj {
    private final static NamedThreadLocal<ThreadObj> HOLDER = new NamedThreadLocal<>("loginContext");

    private static ThreadLocal<ThreadObj> getHolder() {
        return HOLDER;
    }

    private String pin;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}