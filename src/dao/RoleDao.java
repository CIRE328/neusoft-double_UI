package dao;

import pojo.Role;
import java.util.List;
import java.util.Optional;

public class RoleDao extends BaseDaoImpl<Role, Integer> {

    public RoleDao() {
        super("role", "id", Role.class);
    }

    //根据角色名称查询
    public Optional<Role> findByName(String name) {
        String sql = "SELECT * FROM role WHERE name = ? AND is_deleted = 0";
        List<Role> list = executeQuery(sql, name);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}