package service;

import dao.*;
import pojo.*;
import java.util.*;
import java.util.stream.Collectors;

public class MealService {
    private final FoodDao foodDao = new FoodDao();
    private final PreferenceDao preferenceDao = new PreferenceDao();
    private final MealDao mealDao = new MealDao();
    private final CustomerDao customerDao = new CustomerDao();

    public List<Food> findAllFoods() { return foodDao.findAll(); }
    public List<Food> findFoodsByName(String keyword) {
        if (keyword == null || keyword.isEmpty()) return findAllFoods();
        return foodDao.findAll().stream()
                .filter(f -> f.getFoodName() != null && f.getFoodName().contains(keyword))
                .collect(Collectors.toList());
    }
    public List<Food> findFoodsByType(String foodType) {
        return foodDao.findAll().stream()
                .filter(f -> f.getFoodType() != null && f.getFoodType().equals(foodType))
                .collect(Collectors.toList());
    }
    public Food addFood(Food food) { return foodDao.insert(food); }
    public boolean updateFood(Food food) {
        if (foodDao.findById(food.getId()).isEmpty()) return false;
        foodDao.update(food);
        return true;
    }
    public boolean deleteFood(Integer foodId) { return foodDao.deleteById(foodId); }

    public Optional<Preference> findPreferenceByCustomerId(Integer customerId) {
        return preferenceDao.findAll().stream()
                .filter(p -> p.getCustomerId().equals(customerId))
                .findFirst();
    }
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
    public boolean deletePreference(Integer customerId) {
        Optional<Preference> opt = findPreferenceByCustomerId(customerId);
        return opt.filter(p -> preferenceDao.deleteById(p.getId())).isPresent();
    }

    public List<Meal> findMealsByWeekDay(String weekDay) {
        return mealDao.findAll().stream()
                .filter(m -> m.getWeekDay().equals(weekDay))
                .collect(Collectors.toList());
    }
    public Optional<Meal> findMeal(String weekDay, Integer mealType) {
        return mealDao.findAll().stream()
                .filter(m -> m.getWeekDay().equals(weekDay) && m.getMealType().equals(mealType))
                .findFirst();
    }
    public boolean scheduleMeal(Meal meal) {
        if (meal.getFoodId() != null && foodDao.findById(meal.getFoodId()).isEmpty()) return false;
        if (meal.getId() == null) mealDao.insert(meal);
        else mealDao.update(meal);
        return true;
    }
    public boolean removeMealSchedule(String weekDay, Integer mealType) {
        Optional<Meal> opt = findMeal(weekDay, mealType);
        return opt.map(m -> mealDao.deleteById(m.getId())).orElse(false);
    }
    public List<Meal> recommendMealsForCustomer(Integer customerId, String weekDay) {
        return findMealsByWeekDay(weekDay);
    }
}