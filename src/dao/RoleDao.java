package dao;

import pojo.Role;
import java.util.List;
import java.util.Optional;

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

    /**
     * 根据角色名称查询角色
     * 角色名称唯一，最多返回一个角色
     *
     * @param name 角色名称
     * @return 包含查询结果的 Optional 对象
     */

    public Optional<Role> findByName(String name) {
        String sql = "SELECT * FROM role WHERE name = ? AND is_deleted = 0";
        List<Role> list = executeQuery(sql, name);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
