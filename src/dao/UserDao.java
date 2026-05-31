package dao;

import pojo.User;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问对象
 * 提供系统用户相关的数据库操作，包括根据用户名查询等
 */

public class UserDao extends BaseDaoImpl<User, Integer> {

    /**
     * 构造函数
     * 初始化用户 DAO，指定表名、主键列名、实体类类型，使用逻辑删除
     */

    public UserDao() {
        super("user", "id", User.class);
    }

    /**
     * 根据用户名查询用户
     * 用户名唯一，最多返回一个用户
     *
     * @param username 用户名
     * @return 包含查询结果的 Optional 对象
     */

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ? AND is_deleted = 0";
        List<User> list = executeQuery(sql, username);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
