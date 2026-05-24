package dao;

import pojo.Meal;
import java.util.List;

public class MealDao extends BaseDaoImpl<Meal, Integer> {

    public MealDao() {
        super("meal", "id", Meal.class);
    }

    //根据星期几查询
    public List<Meal> findByWeekDay(String weekDay) {
        String sql = "SELECT * FROM meal WHERE week_day = ? AND is_deleted = 0";
        return executeQuery(sql, weekDay);
    }

    //根据食品ID查询被哪些餐次使用
    public List<Meal> findByFoodId(Integer foodId) {
        String sql = "SELECT * FROM meal WHERE food_id = ? AND is_deleted = 0";
        return executeQuery(sql, foodId);
    }
}