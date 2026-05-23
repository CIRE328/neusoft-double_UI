package com.neusoft.dao;

import com.neusoft.pojo.User;
import java.util.List;
import java.util.Optional;

public class UserDao extends BaseDaoImpl<User, Integer> {

    public UserDao() {
        super("user", "id", User.class, false);
    }

    // 自定义查询：根据用户名查找
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ? AND is_deleted = 0";
        List<User> list = executeQuery(sql, username);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}