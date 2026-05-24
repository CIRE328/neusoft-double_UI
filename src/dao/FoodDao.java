package dao;

import pojo.Food;
import java.util.List;

public class FoodDao extends BaseDaoImpl<Food, Integer> {

    public FoodDao() {
        super("food", "id", Food.class, false);
    }

    //根据食品类型查询
    public List<Food> findByType(String foodType) {
        String sql = "SELECT * FROM food WHERE food_type = ?";
        return executeQuery(sql, foodType);
    }

    //根据名称模糊查询
    public List<Food> findByNameLike(String keyword) {
        String sql = "SELECT * FROM food WHERE food_name LIKE ?";
        return executeQuery(sql, "%" + keyword + "%");
    }
}