package view.panel;

import pojo.Food;
import service.MealService;
import view.component.ButtonColumn;
import view.dialog.FoodDialog;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class FoodPanel extends JPanel {
    private MealService mealService;
    private JTable table;
    private DefaultTableModel tableModel;

    public FoodPanel(MealService mealService) {
        this.mealService = mealService;
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        String[] cols = {"ID", "食品名称", "类型", "价格", "是否清真", "操作"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 5; }
        };
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        new ButtonColumn(table, "编辑", 5, this::editFood);
        new ButtonColumn(table, "删除", 6, this::deleteFood);

        JButton addBtn = new JButton("新增食品");
        addBtn.addActionListener(e -> addFood());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(addBtn);
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Food> list = mealService.findAllFoods();
        for (Food f : list) {
            String halal = f.getIsHalal() == 1 ? "是" : "否";
            tableModel.addRow(new Object[]{
                    f.getId(), f.getFoodName(), f.getFoodType(),
                    f.getPrice(), halal, "编辑", "删除"
            });
        }
        TableUtils.autoResizeColumns(table);
    }

    private void addFood() {
        FoodDialog dialog = new FoodDialog(SwingUtilities.getWindowAncestor(this), mealService, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) loadData();
    }

    private void editFood(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        Food food = mealService.findAllFoods().stream()
                .filter(f -> f.getId().equals(id)).findFirst().orElse(null);
        if (food != null) {
            FoodDialog dialog = new FoodDialog(SwingUtilities.getWindowAncestor(this), mealService, food);
            dialog.setVisible(true);
            if (dialog.isSuccess()) loadData();
        }
    }

    private void deleteFood(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        if (UIUtils.confirm(this, "确定删除该食品吗？")) {
            boolean success = mealService.deleteFood(id);
            if (success) loadData();
            else UIUtils.showError(this, "删除失败");
        }
    }
}
