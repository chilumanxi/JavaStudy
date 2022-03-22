package github.chilumanxi.javasdk;


import github.chilumanxi.javasdk.entity.UserManager;
import github.chilumanxi.javasdk.entity.impl.UserManagerImpl;
import github.chilumanxi.javasdk.proxy.SecurityHandler;

public class Client {
    public static void main(String[] args){
        SecurityHandler handler = new SecurityHandler();
        UserManager userManager = (UserManager) handler.createProxyInstance(new UserManagerImpl());
        userManager.addUser("xiaoMing", "1234567");
        userManager.delUser(123);
    }
}
