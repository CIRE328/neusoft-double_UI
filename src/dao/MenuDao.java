package dao;

import pojo.Menu;
import java.util.List;

/**
 * 菜单数据访问对象
 * 提供系统菜单相关的数据库操作，包括按父级菜单查询子菜单等
 */

public class MenuDao extends BaseDaoImpl<Menu, Integer> {

    /**
     * 构造函数
     * 初始化菜单 DAO，指定表名、主键列名、实体类类型，不使用逻辑删除
     */

    public MenuDao() {
        super("menu", "id", Menu.class, false);
    }

    /**
     * 根据父级菜单ID查询子菜单
     *
     * @param parentId 父级菜单ID
     * @return 该父菜单下的子菜单列表
     */

    public List<Menu> findByParentId(Integer parentId) {
        String sql = "SELECT * FROM menu WHERE parent_id = ?";
        return executeQuery(sql, parentId);
    }
}
