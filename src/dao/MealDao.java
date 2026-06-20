package dao;

import pojo.Meal;
import java.util.List;

/**
 * 餐次数据访问对象
 * 提供餐次相关的数据库操作，包括按星期、按食品查询等
 */

public class MealDao extends BaseDaoImpl<Meal, Integer> {

    /**
     * 构造函数
     * 初始化餐次 DAO，指定表名、主键列名、实体类类型，使用逻辑删除
     */

    public MealDao() {
        super("meal", "id", Meal.class);
    }

    /**
     * 根据星期几查询餐次
     *
     * @param weekDay 星期几（如周一、周二）
     * @return 该星期的餐次列表
     */

    public List<Meal> findByWeekDay(String weekDay) {
        String sql = "SELECT * FROM meal WHERE week_day = ? AND is_deleted = 0";
        return executeQuery(sql, weekDay);
    }

}
