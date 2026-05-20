package com.neusoft.entity;

public class Meal {
    private Integer id;
    private String weekDay;
    private Integer foodId;
    private Integer mealType;
    private String taste;
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
