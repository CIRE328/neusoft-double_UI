package dao;

import pojo.RoleMenu;
import java.util.List;

/**
 * 角色菜单权限数据访问对象
 * 提供角色与菜单关联关系的数据库操作
 */

public class RoleMenuDao extends BaseDaoImpl<RoleMenu, Integer> {

    /**
     * 构造函数
     * 初始化角色菜单 DAO，指定表名、主键列名、实体类类型，不使用逻辑删除
     */

    public RoleMenuDao() {
        super("rolemenu", "id", RoleMenu.class, false);
    }

    /**
     * 根据角色ID查询菜单权限
     *
     * @param roleId 角色ID
     * @return 该角色拥有的菜单权限列表
     */

    public List<RoleMenu> findByRoleId(Integer roleId) {
        String sql = "SELECT * FROM rolemenu WHERE role_id = ?";
        return executeQuery(sql, roleId);
    }

}
