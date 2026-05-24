package dao;

import pojo.Menu;
import java.util.List;

public class MenuDao extends BaseDaoImpl<Menu, Integer> {

    public MenuDao() {
        super("menu", "id", Menu.class, false); // 不支持逻辑删除
    }

    public List<Menu> findByParentId(Integer parentId) {
        String sql = "SELECT * FROM menu WHERE parent_id = ?";
        return executeQuery(sql, parentId);
    }
}