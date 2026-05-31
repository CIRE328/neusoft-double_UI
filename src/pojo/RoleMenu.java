package pojo;

/**
 * 角色菜单关联实体类
 * 对应数据库表 rolemenu，维护角色与菜单的多对多关系
 */

public class RoleMenu {
    private Integer id;
    /** 角色ID，关联 role 表 */
    private Integer roleId;
    /** 菜单ID，关联 menu 表 */
    private Integer menu;

    public RoleMenu() {}

    public RoleMenu(Integer id, Integer roleId, Integer menu) {
        this.id = id;
        this.roleId = roleId;
        this.menu = menu;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }

    public Integer getMenu() { return menu; }
    public void setMenu(Integer menu) { this.menu = menu; }
}