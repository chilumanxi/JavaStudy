package github.chilumanxi.cglib;

import github.chilumanxi.cglib.entity.impl.UserManagerImpl;
import github.chilumanxi.cglib.proxy.CglibProxy;

public class Client {
    public static void main(String[] args){
        CglibProxy proxy = new CglibProxy();
        UserManagerImpl userManager = (UserManagerImpl) proxy.getProxy(UserManagerImpl.class);
        userManager.addUser("xiaoMing", "123456");
        userManager.delUser(123);
    }
}
