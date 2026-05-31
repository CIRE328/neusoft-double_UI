package service;

import dao.UserDao;
import pojo.User;

/**
 * 认证服务，负责用户登录凭据校验与身份验证。
 */
public class AuthService {
    private final UserDao userDao = new UserDao();

    /**
     * 根据用户名和密码验证用户身份。
     *
     * @param username 用户名
     * @param password 密码
     * @return 验证成功返回用户对象，失败或参数为空时返回 null
     */
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
