package com.neusoft.service;

import com.neusoft.dao.UserDao;
import com.neusoft.pojo.User;
import com.neusoft.util.DateUtils;

import java.util.*;

public class UserService {
    private final UserDao userDao = new UserDao();

    public List<User> findAllUsers() { return userDao.findAll(); }
    public Optional<User> findUserById(Integer id) { return userDao.findById(id); }

    public List<User> findUsersByRole(Integer roleId) {
        return userDao.findAll().stream()
                .filter(u -> u.getRoleId().equals(roleId))
                .toList();
    }

    public List<User> findUsersByName(String keyword) {
        if (keyword == null || keyword.isEmpty()) return findAllUsers();
        return userDao.findAll().stream()
                .filter(u -> u.getNickname() != null && u.getNickname().contains(keyword))
                .toList();
    }

    public User addUser(User user) {
        if (userDao.findAll().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            System.err.println("用户名已存在");
            return null;
        }
        if (user.getPhoneNumber() != null && user.getPhoneNumber().length() >= 6) {
            user.setPassword(user.getPhoneNumber().substring(user.getPhoneNumber().length() - 6));
        } else {
            user.setPassword("123456");
        }
        user.setCreateTime(DateUtils.now());
        user.setUpdateTime(DateUtils.now());
        user.setIsDeleted(0);
        return userDao.insert(user);
    }

    public boolean updateUser(User user) {
        if (userDao.findById(user.getId()).isEmpty()) return false;
        user.setUpdateTime(DateUtils.now());
        userDao.update(user);
        return true;
    }

    public boolean resetPassword(Integer userId) {
        Optional<User> opt = userDao.findById(userId);
        if (opt.isEmpty()) return false;
        User user = opt.get();
        if (user.getPhoneNumber() != null && user.getPhoneNumber().length() >= 6) {
            user.setPassword(user.getPhoneNumber().substring(user.getPhoneNumber().length() - 6));
        } else {
            user.setPassword("123456");
        }
        userDao.update(user);
        return true;
    }

    public boolean deleteUser(Integer userId) {
        return userDao.deleteById(userId);
    }
}