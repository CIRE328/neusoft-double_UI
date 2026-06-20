package service;

import dao.UserDao;
import pojo.User;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务类
 * 提供用户管理相关的业务逻辑功能
 * 包括用户查询、添加、更新、删除、密码重置等功能
 * 支持管理员和健康管家两种角色
 */

public class UserService {
    private final UserDao userDao = new UserDao();

    /**
     * 初始化三个默认管理员
     * 如果系统中不存在admin、admin1、admin2这三个管理员账号，则自动创建
     * 默认密码与用户名相同，角色为管理员
     */

    public void initAdmins() {
        String[] adminNames = {"admin", "admin1", "admin2"};
        for (String name : adminNames) {
            if (userDao.findAll().stream().noneMatch(u -> u.getUsername().equals(name))) {
                User admin = new User();
                admin.setUsername(name);
                admin.setPassword(name);          // 密码与用户名相同
                admin.setNickname("系统管理员");
                admin.setPhoneNumber("13800000000");
                admin.setEmail("admin@example.com");
                admin.setSex(1);
                admin.setRoleId(1);               // 管理员
                admin.setCreateTime(new Date());
                admin.setUpdateTime(new Date());
                admin.setCreateBy(1);
                admin.setUpdateBy(1);
                admin.setIsDeleted(0);
                userDao.insert(admin);
                System.out.println("默认管理员已创建: " + name);
            }
        }
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */

    public List<User> findAllUsers() {
        return userDao.findAll();
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 包含用户的Optional对象
     */

    public Optional<User> findUserById(Integer id) {
        return userDao.findById(id);
    }

    /**
     * 根据角色ID查询用户
     *
     * @param roleId 角色ID（1-管理员，2-健康管家）
     * @return 用户列表
     */

    public List<User> findUsersByRole(Integer roleId) {
        return userDao.findAll().stream()
                .filter(u -> u.getRoleId().equals(roleId))
                .toList();
    }

    /**
     * 根据姓名模糊查询用户
     *
     * @param keyword 用户姓名关键词
     * @return 用户列表
     */

    public List<User> findUsersByName(String keyword) {
        if (keyword == null || keyword.isEmpty()) return findAllUsers();
        return userDao.findAll().stream()
                .filter(u -> u.getNickname() != null && u.getNickname().contains(keyword))
                .toList();
    }

    /**
     * 添加用户
     * 只能添加健康管家（roleId=2），不能手动添加管理员
     * 密码默认为手机号后6位，如果手机号不足6位则使用默认密码123456
     *
     * @param user 用户信息
     * @return 添加后的用户对象，添加失败返回null
     */

    public User addUser(User user) {
        if (user.getRoleId() == 1) {
            System.err.println("不能手动添加管理员");
            return null;
        }
        if (userDao.findAll().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            System.err.println("用户名已存在");
            return null;
        }
        // 密码默认手机号后6位
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            if (user.getPhoneNumber() != null && user.getPhoneNumber().length() >= 6) {
                user.setPassword(user.getPhoneNumber().substring(user.getPhoneNumber().length() - 6));
            } else {
                user.setPassword("123456");
            }
        }
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setCreateBy(1);      // 默认系统管理员ID=1
        user.setUpdateBy(1);
        user.setIsDeleted(0);
        return userDao.insert(user);
    }

    /**
     * 更新用户信息
     * 不能修改管理员的信息
     *
     * @param user 用户信息
     * @return 是否更新成功
     */

    public boolean updateUser(User user) {
        Optional<User> existingOpt = userDao.findById(user.getId());
        if (existingOpt.isEmpty()) return false;
        User existing = existingOpt.get();
        if (existing.getRoleId() == 1) {
            System.err.println("不能修改管理员信息");
            return false;
        }
        user.setUpdateTime(new Date());
        userDao.update(user);
        return true;
    }

    /**
     * 重置用户密码
     * 不能重置管理员密码
     * 密码重置为手机号后6位，如果手机号不足6位则使用默认密码123456
     *
     * @param userId 用户ID
     * @return 是否重置成功
     */

    public boolean resetPassword(Integer userId) {
        Optional<User> opt = userDao.findById(userId);
        if (opt.isEmpty()) return false;
        User user = opt.get();
        if (user.getRoleId() == 1) {
            System.err.println("不能重置管理员密码");
            return false;
        }
        if (user.getPhoneNumber() != null && user.getPhoneNumber().length() >= 6) {
            user.setPassword(user.getPhoneNumber().substring(user.getPhoneNumber().length() - 6));
        } else {
            user.setPassword("123456");
        }
        user.setUpdateTime(new Date());
        user.setUpdateBy(1); // 假设当前操作用户ID=1，或从登录用户获取
        userDao.update(user);
        return true;
    }

    /**
     * 删除用户
     * 不能删除管理员
     *
     * @param userId 用户ID
     * @return 是否删除成功
     */

    public boolean deleteUser(Integer userId) {
        Optional<User> opt = userDao.findById(userId);
        if (opt.isEmpty()) return false;
        if (opt.get().getRoleId() == 1) {
            System.err.println("不能删除管理员");
            return false;
        }
        return userDao.deleteById(userId);
    }
}