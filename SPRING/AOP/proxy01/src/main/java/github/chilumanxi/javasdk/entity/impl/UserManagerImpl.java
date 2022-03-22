package github.chilumanxi.javasdk.entity.impl;


import github.chilumanxi.javasdk.entity.UserManager;

public class UserManagerImpl implements UserManager {
    @Override
    public void addUser(String username, String password) {
        System.out.println("-------------add User-------------");
    }

    @Override
    public void delUser(int userId) {
        System.out.println("-------------Delete User-------------");
    }
}
