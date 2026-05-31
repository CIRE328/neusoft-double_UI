package dao;

import pojo.NurseLevelItem;
import java.util.List;

/**
 * 护理级别项目关联数据访问对象
 * 提供护理级别与护理项目关联关系的数据库操作
 */

public class NurseLevelItemDao extends BaseDaoImpl<NurseLevelItem, Integer> {

    /**
     * 构造函数
     * 初始化护理级别项目关联 DAO，指定表名、主键列名、实体类类型，不使用逻辑删除
     */

    public NurseLevelItemDao() {
        super("nurselevelitem", "id", NurseLevelItem.class, false);
    }

    /**
     * 根据护理级别ID查询关联的护理项目
     *
     * @param levelId 护理级别ID
     * @return 该级别关联的护理项目列表
     */

    public List<NurseLevelItem> findByLevelId(Integer levelId) {
        String sql = "SELECT * FROM nurselevelitem WHERE level_id = ?";
        return executeQuery(sql, levelId);
    }

    /**
     * 根据护理项目ID查询被哪些级别引用
     *
     * @param itemId 护理项目ID
     * @return 引用该项目的级别关联列表
     */

    public List<NurseLevelItem> findByItemId(Integer itemId) {
        String sql = "SELECT * FROM nurselevelitem WHERE item_id = ?";
        return executeQuery(sql, itemId);
    }

    /**
     * 删除某个护理级别的所有项目关联
     *
     * @param levelId 护理级别ID
     * @return 受影响的行数
     */

    public int deleteByLevelId(Integer levelId) {
        String sql = "DELETE FROM nurselevelitem WHERE level_id = ?";
        return executeUpdate(sql, levelId);
    }
}
