package github.chilumanxi.cglib.entity.impl;


import github.chilumanxi.cglib.entity.UserManager;

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
