package view.panel;

import pojo.Food;
import pojo.Meal;
import service.CustomerService;
import service.MealService;
import view.component.ButtonColumn;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * 膳食日历面板
 * 按星期管理早中晚餐次安排，支持按客户喜好推荐餐次
 */

public class MealCalendarPanel extends JPanel {
    private MealService mealService;
    private CustomerService customerService = new CustomerService();
    private JComboBox<String> weekCombo;
    private JComboBox<String> customerCombo;
    private JTable table;
    private DefaultTableModel tableModel;

    private final String[] weeks = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private final String[] mealTypes = {"早餐", "午餐", "晚餐"};

    /**
     * 构造函数
     *
     * @param mealService 膳食服务
     */

    public MealCalendarPanel(MealService mealService) {
        this.mealService = mealService;
        setLayout(new BorderLayout());
        initUI();
        loadData(weeks[0]);
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("选择星期:"));
        weekCombo = new JComboBox<>(weeks);
        weekCombo.addActionListener(e -> {
            String week = (String) weekCombo.getSelectedItem();
            loadData(week);
        });
        topPanel.add(weekCombo);

        // 客户选择（用于推荐）
        topPanel.add(new JLabel("   选择客户:"));
        customerCombo = new JComboBox<>();
        // 加载所有客户
        customerService.findAllCustomers().forEach(c ->
                customerCombo.addItem(c.getId() + " - " + c.getCustomerName()));
        topPanel.add(customerCombo);

        JButton recommendBtn = new JButton("按喜好推荐");
        recommendBtn.addActionListener(e -> showRecommendation());
        topPanel.add(recommendBtn);

        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> {
            String week = (String) weekCombo.getSelectedItem();
            loadData(week);
        });
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"餐次", "食品ID", "食品名称", "口味", "编辑", "删除"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 4 || col == 5; }
        };
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        new ButtonColumn(table, "编辑", 4, this::editMeal);
        new ButtonColumn(table, "删除", 5, this::deleteMeal);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addBtn = new JButton("新增餐次安排");
        addBtn.addActionListener(e -> addMeal());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadData(String weekDay) {
        tableModel.setRowCount(0);
        List<Meal> meals = mealService.findMealsByWeekDay(weekDay);
        for (Meal m : meals) {
            String foodName = "";
            if (m.getFoodId() != null) {
                Food food = mealService.findAllFoods().stream()
                        .filter(f -> f.getId().equals(m.getFoodId())).findFirst().orElse(null);
                if (food != null) foodName = food.getFoodName();
            }
            tableModel.addRow(new Object[]{
                    mealTypes[m.getMealType() - 1], m.getFoodId(), foodName,
                    m.getTaste() == null ? "" : m.getTaste(), "编辑", "删除"
            });
        }
        TableUtils.autoResizeColumns(table);
    }

    private void showRecommendation() {
        String selected = (String) customerCombo.getSelectedItem();
        if (selected == null) {
            UIUtils.showError(this, "请选择客户");
            return;
        }
        Integer customerId = Integer.parseInt(selected.split(" - ")[0]);
        String week = (String) weekCombo.getSelectedItem();
        // 调用 recommendMealsForCustomer
        List<Meal> recommended = mealService.recommendMealsForCustomer(customerId, week);
        if (recommended.isEmpty()) {
            UIUtils.showInfo(this, "当前没有安排任何餐次");
            return;
        }
        StringBuilder sb = new StringBuilder("推荐餐次（当前安排）：\n");
        for (Meal m : recommended) {
            String mealTypeName = m.getMealType() == 1 ? "早餐" : (m.getMealType() == 2 ? "午餐" : "晚餐");
            sb.append(mealTypeName).append(": ");
            Food food = mealService.findAllFoods().stream()
                    .filter(f -> f.getId().equals(m.getFoodId())).findFirst().orElse(null);
            sb.append(food != null ? food.getFoodName() : "未知");
            sb.append(" (").append(m.getTaste() != null ? m.getTaste() : "默认口味").append(")\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "推荐餐次", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addMeal() {
        String week = (String) weekCombo.getSelectedItem();
        Object[] options = mealTypes;
        int typeIndex = JOptionPane.showOptionDialog(this, "选择餐次", "新增",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (typeIndex < 0) return;
        int mealType = typeIndex + 1;
        List<Food> foods = mealService.findAllFoods();
        String[] foodNames = foods.stream().map(f -> f.getId() + " - " + f.getFoodName()).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this, "选择食品", "食品",
                JOptionPane.QUESTION_MESSAGE, null, foodNames, foodNames[0]);
        if (selected == null) return;
        Integer foodId = Integer.parseInt(selected.split(" - ")[0]);
        String taste = JOptionPane.showInputDialog(this, "口味 (如多糖/少糖/多盐/少盐):");
        Meal meal = new Meal();
        meal.setWeekDay(week);
        meal.setMealType(mealType);
        meal.setFoodId(foodId);
        meal.setTaste(taste);
        meal.setIsDeleted(0);
        boolean success = mealService.scheduleMeal(meal);
        if (success) {
            UIUtils.showInfo(this, "添加成功");
            loadData(week);
        } else {
            UIUtils.showError(this, "添加失败");
        }
    }

    private void editMeal(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        String week = (String) weekCombo.getSelectedItem();
        Integer mealType = null;
        for (int i = 0; i < mealTypes.length; i++) {
            if (mealTypes[i].equals(tableModel.getValueAt(row, 0))) {
                mealType = i + 1;
                break;
            }
        }
        if (mealType == null) return;
        List<Food> foods = mealService.findAllFoods();
        String[] foodNames = foods.stream().map(f -> f.getId() + " - " + f.getFoodName()).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this, "选择食品", "编辑",
                JOptionPane.QUESTION_MESSAGE, null, foodNames, foodNames[0]);
        if (selected == null) return;
        Integer foodId = Integer.parseInt(selected.split(" - ")[0]);
        String taste = JOptionPane.showInputDialog(this, "口味:", tableModel.getValueAt(row, 3));
        Meal meal = mealService.findMeal(week, mealType).orElse(null);
        if (meal != null) {
            meal.setFoodId(foodId);
            meal.setTaste(taste);
            mealService.scheduleMeal(meal);
            UIUtils.showInfo(this, "修改成功");
            loadData(week);
        }
    }

    private void deleteMeal(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        String week = (String) weekCombo.getSelectedItem();
        Integer mealType = null;
        for (int i = 0; i < mealTypes.length; i++) {
            if (mealTypes[i].equals(tableModel.getValueAt(row, 0))) {
                mealType = i + 1;
                break;
            }
        }
        if (mealType == null) return;
        if (UIUtils.confirm(this, "确定删除该餐次安排吗？")) {
            boolean success = mealService.removeMealSchedule(week, mealType);
            if (success) {
                UIUtils.showInfo(this, "删除成功");
                loadData(week);
            } else {
                UIUtils.showError(this, "删除失败");
            }
        }
    }
}