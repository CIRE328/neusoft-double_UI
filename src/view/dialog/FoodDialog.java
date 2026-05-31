package view.dialog;

import pojo.Food;
import service.MealService;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class FoodDialog extends JDialog {
    private MealService mealService;
    private Food food;
    private boolean success = false;

    private JTextField nameField, typeField, priceField;
    private JComboBox<String> halalCombo;

    public FoodDialog(Window owner, MealService mealService, Food food) {
        super(owner, food == null ? "新增食品" : "编辑食品", ModalityType.APPLICATION_MODAL);
        this.mealService = mealService;
        this.food = food;
        setSize(350, 250);
        setLocationRelativeTo(owner);
        initUI();
        if (food != null) loadData();
    }

    private void initUI() {
        setLayout(new GridLayout(5, 2, 10, 10));
        add(new JLabel("食品名称:"));
        nameField = new JTextField();
        add(nameField);
        add(new JLabel("类型:"));
        typeField = new JTextField();
        add(typeField);
        add(new JLabel("价格:"));
        priceField = new JTextField();
        add(priceField);
        add(new JLabel("是否清真:"));
        halalCombo = new JComboBox<>(new String[]{"否", "是"});
        add(halalCombo);
        JButton okBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        add(okBtn);
        add(cancelBtn);
        okBtn.addActionListener(e -> save());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void loadData() {
        nameField.setText(food.getFoodName());
        typeField.setText(food.getFoodType());
        priceField.setText(food.getPrice().toString());
        halalCombo.setSelectedIndex(food.getIsHalal() == 1 ? 1 : 0);
    }

    private void save() {
        try {
            if (food == null) food = new Food();
            food.setFoodName(nameField.getText().trim());
            food.setFoodType(typeField.getText().trim());
            food.setPrice(new BigDecimal(priceField.getText().trim()));
            food.setIsHalal(halalCombo.getSelectedIndex());
            if (food.getId() == null) {
                mealService.addFood(food);
            } else {
                mealService.updateFood(food);
            }
            success = true;
            dispose();
        } catch (Exception ex) {
            UIUtils.showError(this, "保存失败: " + ex.getMessage());
        }
    }

    public boolean isSuccess() { return success; }
}