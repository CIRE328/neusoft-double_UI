package com.neusoft.dao;

import com.neusoft.pojo.NurseLevelItem;
import java.util.List;

public class NurseLevelItemDao extends BaseDaoImpl<NurseLevelItem, Integer> {

    public NurseLevelItemDao() {
        super("nurse_level_item", "id", NurseLevelItem.class, false);
    }

    //根据级别ID查询关联的项目
    public List<NurseLevelItem> findByLevelId(Integer levelId) {
        String sql = "SELECT * FROM nurse_level_item WHERE level_id = ?";
        return executeQuery(sql, levelId);
    }

    //根据项目ID查询被哪些级别引用
    public List<NurseLevelItem> findByItemId(Integer itemId) {
        String sql = "SELECT * FROM nurse_level_item WHERE item_id = ?";
        return executeQuery(sql, itemId);
    }

    //删除某个级别的所有项目关联
    public int deleteByLevelId(Integer levelId) {
        String sql = "DELETE FROM nurse_level_item WHERE level_id = ?";
        return executeUpdate(sql, levelId);
    }
}