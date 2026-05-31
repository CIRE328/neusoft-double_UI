package pojo;

/**
 * 膳食安排实体类
 * 对应数据库表 meal，记录每周各餐次的菜品配置
 */

public class Meal {
    private Integer id;
    private String weekDay;
    /** 食品ID，关联 food 表 */
    private Integer foodId;
    /** 餐次类型：1-早餐，2-午餐，3-晚餐 */
    private Integer mealType;
    private String taste;
    /** 逻辑删除标志：0-未删除，1-已删除 */
    private Integer isDeleted;

    public Meal() {}

    public Meal(Integer id, String weekDay, Integer foodId, Integer mealType,
                String taste, Integer isDeleted) {
        this.id = id;
        this.weekDay = weekDay;
        this.foodId = foodId;
        this.mealType = mealType;
        this.taste = taste;
        this.isDeleted = isDeleted;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getWeekDay() { return weekDay; }
    public void setWeekDay(String weekDay) { this.weekDay = weekDay; }

    public Integer getFoodId() { return foodId; }
    public void setFoodId(Integer foodId) { this.foodId = foodId; }

    public Integer getMealType() { return mealType; }
    public void setMealType(Integer mealType) { this.mealType = mealType; }

    public String getTaste() { return taste; }
    public void setTaste(String taste) { this.taste = taste; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
