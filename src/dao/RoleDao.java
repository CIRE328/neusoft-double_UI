package dao;

import pojo.Role;

/**
 * 角色数据访问对象
 * 提供角色相关的数据库操作，包括根据角色名称查询等
 */

public class RoleDao extends BaseDaoImpl<Role, Integer> {

    /**
     * 构造函数
     * 初始化角色 DAO，指定表名、主键列名、实体类类型，使用逻辑删除
     */

    public RoleDao() {
        super("role", "id", Role.class);
    }

}
