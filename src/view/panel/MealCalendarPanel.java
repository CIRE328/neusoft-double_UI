package view.panel;

import pojo.Food;
import pojo.Meal;
import service.MealService;
import view.component.ButtonColumn;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MealCalendarPanel extends JPanel {
    private MealService mealService;
    private JComboBox<String> weekCombo;
    private JTable table;
    private DefaultTableModel tableModel;

    private final String[] weeks = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private final String[] mealTypes = {"早餐", "午餐", "晚餐"};

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
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> {
            String week = (String) weekCombo.getSelectedItem();
            loadData(week);
        });
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"餐次", "食品ID", "食品名称", "口味", "操作"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 4; }
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
    }

    private void addMeal() {
        String week = (String) weekCombo.getSelectedItem();
        Object[] options = mealTypes;
        int typeIndex = JOptionPane.showOptionDialog(this, "选择餐次", "新增",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (typeIndex < 0) return;
        int mealType = typeIndex + 1;
        // 选择食品
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
