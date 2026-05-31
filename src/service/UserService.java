package service;

import dao.UserDao;
import pojo.User;
import util.DateUtils;

import java.util.*;
import java.util.stream.Collectors;  // 添加这行导入

/**
 * 用户管理服务，负责系统用户的查询、新增、修改、密码重置及删除。
 */
public class UserService {
    private final UserDao userDao = new UserDao();

    /**
     * 查询所有用户。
     *
     * @return 用户列表
     */
    public List<User> findAllUsers() { return userDao.findAll(); }

    /**
     * 根据 ID 查询用户。
     *
     * @param id 用户 ID
     * @return 存在则返回用户，否则为空
     */
    public Optional<User> findUserById(Integer id) { return userDao.findById(id); }

    /**
     * 按角色 ID 筛选用户。
     *
     * @param roleId 角色 ID
     * @return 匹配该角色的用户列表
     */
    public List<User> findUsersByRole(Integer roleId) {
        return userDao.findAll().stream()
                .filter(u -> u.getRoleId().equals(roleId))
                .collect(Collectors.toList());  // 改这里
    }

    /**
     * 按昵称关键字模糊查询用户。
     *
     * @param keyword 昵称关键字，为空时返回全部用户
     * @return 匹配的用户列表
     */
    public List<User> findUsersByName(String keyword) {
        if (keyword == null || keyword.isEmpty()) return findAllUsers();
        return userDao.findAll().stream()
                .filter(u -> u.getNickname() != null && u.getNickname().contains(keyword))
                .collect(Collectors.toList());  // 改这里
    }

    /**
     * 新增用户，自动设置初始密码（手机号后六位或默认 123456）。
     *
     * @param user 待新增的用户信息
     * @return 新增成功返回用户对象，用户名已存在时返回 null
     */
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

    /**
     * 更新用户信息。
     *
     * @param user 待更新的用户对象
     * @return 更新成功返回 true，用户不存在时返回 false
     */
    public boolean updateUser(User user) {
        if (userDao.findById(user.getId()).isEmpty()) return false;
        user.setUpdateTime(DateUtils.now());
        userDao.update(user);
        return true;
    }

    /**
     * 重置用户密码为手机号后六位或默认 123456。
     *
     * @param userId 用户 ID
     * @return 重置成功返回 true，用户不存在时返回 false
     */
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

    /**
     * 逻辑删除指定用户。
     *
     * @param userId 用户 ID
     * @return 删除成功返回 true，否则返回 false
     */
    public boolean deleteUser(Integer userId) {
        return userDao.deleteById(userId);
    }
}
