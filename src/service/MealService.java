package service;

import dao.*;
import pojo.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 膳食服务，负责菜品管理、客户饮食偏好维护及每周餐次排班。
 */
public class MealService {
    private final FoodDao foodDao = new FoodDao();
    private final PreferenceDao preferenceDao = new PreferenceDao();
    private final MealDao mealDao = new MealDao();
    private final CustomerDao customerDao = new CustomerDao();

    /**
     * 查询所有菜品。
     *
     * @return 菜品列表
     */
    public List<Food> findAllFoods() { return foodDao.findAll(); }

    /**
     * 按菜品名称关键字模糊查询。
     *
     * @param keyword 名称关键字，为空时返回全部菜品
     * @return 匹配的菜品列表
     */
    public List<Food> findFoodsByName(String keyword) {
        if (keyword == null || keyword.isEmpty()) return findAllFoods();
        return foodDao.findAll().stream()
                .filter(f -> f.getFoodName() != null && f.getFoodName().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * 按菜品类型筛选。
     *
     * @param foodType 菜品类型
     * @return 该类型的菜品列表
     */
    public List<Food> findFoodsByType(String foodType) {
        return foodDao.findAll().stream()
                .filter(f -> f.getFoodType() != null && f.getFoodType().equals(foodType))
                .collect(Collectors.toList());
    }

    /**
     * 新增菜品。
     *
     * @param food 待新增的菜品信息
     * @return 新增后的菜品对象
     */
    public Food addFood(Food food) { return foodDao.insert(food); }

    /**
     * 更新菜品信息。
     *
     * @param food 待更新的菜品对象
     * @return 更新成功返回 true，菜品不存在时返回 false
     */
    public boolean updateFood(Food food) {
        if (foodDao.findById(food.getId()).isEmpty()) return false;
        foodDao.update(food);
        return true;
    }

    /**
     * 删除指定菜品。
     *
     * @param foodId 菜品 ID
     * @return 删除成功返回 true，否则返回 false
     */
    public boolean deleteFood(Integer foodId) { return foodDao.deleteById(foodId); }

    /**
     * 查询指定客户的饮食偏好。
     *
     * @param customerId 客户 ID
     * @return 存在则返回偏好记录，否则为空
     */
    public Optional<Preference> findPreferenceByCustomerId(Integer customerId) {
        return preferenceDao.findAll().stream()
                .filter(p -> p.getCustomerId().equals(customerId))
                .findFirst();
    }

    /**
     * 保存客户饮食偏好，已存在则更新，否则新增。
     *
     * @param preference 饮食偏好信息
     * @return 保存成功返回 true，客户不存在时返回 false
     */
    public boolean savePreference(Preference preference) {
        if (customerDao.findById(preference.getCustomerId()).isEmpty()) return false;
        Optional<Preference> existing = findPreferenceByCustomerId(preference.getCustomerId());
        if (existing.isPresent()) {
            preference.setId(existing.get().getId());
            preferenceDao.update(preference);
        } else {
            preferenceDao.insert(preference);
        }
        return true;
    }

    /**
     * 删除指定客户的饮食偏好。
     *
     * @param customerId 客户 ID
     * @return 删除成功返回 true，偏好不存在时返回 false
     */
    public boolean deletePreference(Integer customerId) {
        Optional<Preference> opt = findPreferenceByCustomerId(customerId);
        return opt.filter(p -> preferenceDao.deleteById(p.getId())).isPresent();
    }

    /**
     * 查询指定星期几的所有餐次安排。
     *
     * @param weekDay 星期标识
     * @return 该日的餐次列表
     */
    public List<Meal> findMealsByWeekDay(String weekDay) {
        return mealDao.findAll().stream()
                .filter(m -> m.getWeekDay().equals(weekDay))
                .collect(Collectors.toList());
    }

    /**
     * 查询指定星期与餐次类型的单条排班记录。
     *
     * @param weekDay  星期标识
     * @param mealType 餐次类型
     * @return 存在则返回餐次记录，否则为空
     */
    public Optional<Meal> findMeal(String weekDay, Integer mealType) {
        return mealDao.findAll().stream()
                .filter(m -> m.getWeekDay().equals(weekDay) && m.getMealType().equals(mealType))
                .findFirst();
    }

    /**
     * 新增或更新餐次排班，关联菜品须存在。
     *
     * @param meal 餐次排班信息
     * @return 排班成功返回 true，关联菜品不存在时返回 false
     */
    public boolean scheduleMeal(Meal meal) {
        if (meal.getFoodId() != null && foodDao.findById(meal.getFoodId()).isEmpty()) return false;
        if (meal.getId() == null) mealDao.insert(meal);
        else mealDao.update(meal);
        return true;
    }

    /**
     * 删除指定星期与餐次类型的排班。
     *
     * @param weekDay  星期标识
     * @param mealType 餐次类型
     * @return 删除成功返回 true，排班不存在时返回 false
     */
    public boolean removeMealSchedule(String weekDay, Integer mealType) {
        Optional<Meal> opt = findMeal(weekDay, mealType);
        return opt.map(m -> mealDao.deleteById(m.getId())).orElse(false);
    }

    /**
     * 为指定客户推荐某日的餐次（当前返回该日全部排班）。
     *
     * @param customerId 客户 ID
     * @param weekDay    星期标识
     * @return 推荐餐次列表
     */
    public List<Meal> recommendMealsForCustomer(Integer customerId, String weekDay) {
        return findMealsByWeekDay(weekDay);
    }
}
