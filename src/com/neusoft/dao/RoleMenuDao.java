package com.neusoft.dao;

import com.neusoft.pojo.RoleMenu;
import java.util.List;

public class RoleMenuDao extends BaseDaoImpl<RoleMenu, Integer> {

    public RoleMenuDao() {
        super("rolemenu", "id", RoleMenu.class, false);
    }

    //根据角色ID查询菜单权限
    public List<RoleMenu> findByRoleId(Integer roleId) {
        String sql = "SELECT * FROM rolemenu WHERE role_id = ?";
        return executeQuery(sql, roleId);
    }

    //删除某个角色的所有权限
    public int deleteByRoleId(Integer roleId) {
        String sql = "DELETE FROM rolemenu WHERE role_id = ?";
        return executeUpdate(sql, roleId);
    }
}