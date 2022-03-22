package github.chilumanxi.javasdk.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SecurityHandler implements InvocationHandler {
//    定义私有变量：目标对象
    private Object targetObject;

//    产生代理对象
    public Object createProxyInstance(Object targetObject){
        this.targetObject = targetObject;
        return Proxy.newProxyInstance(targetObject.getClass().getClassLoader(), targetObject.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        checkSecurity();

        Object ret = method.invoke(targetObject, args);

        return ret;
    }

    private void checkSecurity() {
        System.out.println("--------------Check Security--------------");
    }
}
