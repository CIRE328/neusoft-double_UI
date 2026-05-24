package com.neusoft.service;

import com.neusoft.dao.UserDao;
import com.neusoft.pojo.User;

public class AuthService {
    private final UserDao userDao = new UserDao();

    public User login(String username, String password) {
        if (username == null || password == null) return null;
        for (User user : userDao.findAll()) {
            if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public boolean isAdmin(User user) {
        return user != null && user.getRoleId() != null && user.getRoleId() == 1;
    }

    public boolean isHousekeeper(User user) {
        return user != null && user.getRoleId() != null && user.getRoleId() == 2;
    }
}