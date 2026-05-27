package service;

import dao.UserDao;
import pojo.User;

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
}