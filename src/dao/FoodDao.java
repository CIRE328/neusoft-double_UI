package dao;

import pojo.Food;
import java.util.List;

/**
 * 食品数据访问对象
 * 提供食品相关的数据库操作，包括按类型、按名称模糊查询等
 */

public class FoodDao extends BaseDaoImpl<Food, Integer> {

    /**
     * 构造函数
     * 初始化食品 DAO，指定表名、主键列名、实体类类型，不使用逻辑删除
     */

    public FoodDao() {
        super("food", "id", Food.class, false);
    }

    /**
     * 根据食品类型查询
     *
     * @param foodType 食品类型
     * @return 该类型的所有食品列表
     */

    public List<Food> findByType(String foodType) {
        String sql = "SELECT * FROM food WHERE food_type = ?";
        return executeQuery(sql, foodType);
    }

}
