package dao;

import pojo.User;

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

}
