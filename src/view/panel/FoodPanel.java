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
import java.util.stream.Collectors;

public class FoodPanel extends JPanel {
    private MealService mealService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JComboBox<String> typeCombo;

    public FoodPanel(MealService mealService) {
        this.mealService = mealService;
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        // 顶部查询栏
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("食品名称:"));
        nameField = new JTextField(10);
        searchPanel.add(nameField);
        searchPanel.add(new JLabel("类型:"));
        typeCombo = new JComboBox<>();
        typeCombo.addItem("全部");
        // 动态加载类型（从已有食品中获取）
        refreshTypeCombo();
        searchPanel.add(typeCombo);
        JButton searchBtn = new JButton("查询");
        JButton refreshBtn = new JButton("刷新");
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);

        JButton addBtn = new JButton("新增食品");
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(addBtn);

        add(searchPanel, BorderLayout.NORTH);

        // 表格
        String[] cols = {"ID", "食品名称", "类型", "价格", "是否清真", "编辑", "删除"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 5 || col == 6; }
        };
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        new ButtonColumn(table, "编辑", 5, this::editFood);
        new ButtonColumn(table, "删除", 6, this::deleteFood);

        add(new JScrollPane(table), BorderLayout.CENTER);

        addBtn.addActionListener(e -> addFood());
        searchBtn.addActionListener(e -> search());
        refreshBtn.addActionListener(e -> loadData());
    }

    private void refreshTypeCombo() {
        // 保留“全部”选项，添加所有类型
        String selected = (String) typeCombo.getSelectedItem();
        typeCombo.removeAllItems();
        typeCombo.addItem("全部");
        List<String> types = mealService.findAllFoods().stream()
                .map(Food::getFoodType)
                .filter(t -> t != null && !t.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        types.forEach(typeCombo::addItem);
        if (selected != null && typeCombo.getItemCount() > 0) {
            typeCombo.setSelectedItem(selected);
        }
    }

    private void loadData() {
        loadData(null, null);
    }

    private void search() {
        String name = nameField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        if ("全部".equals(type)) type = null;
        loadData(name.isEmpty() ? null : name, type);
    }

    private void loadData(String name, String type) {
        tableModel.setRowCount(0);
        List<Food> list;
        if (name != null) {
            // 使用 findFoodsByName
            list = mealService.findFoodsByName(name);
        } else if (type != null) {
            // 使用 findFoodsByType
            list = mealService.findFoodsByType(type);
        } else {
            list = mealService.findAllFoods();
        }
        // 如果同时有 name 和 type，再手动过滤（Service 方法不支持组合）
        if (name != null && type != null) {
            list = list.stream()
                    .filter(f -> f.getFoodType() != null && f.getFoodType().equals(type))
                    .collect(Collectors.toList());
        }
        for (Food f : list) {
            String halal = f.getIsHalal() == 1 ? "是" : "否";
            tableModel.addRow(new Object[]{
                    f.getId(), f.getFoodName(), f.getFoodType(),
                    f.getPrice(), halal, "编辑", "删除"
            });
        }
        TableUtils.autoResizeColumns(table);
        // 刷新类型下拉框，让新添加的类型显示出来
        refreshTypeCombo();
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
            if (success) {
                UIUtils.showInfo(this, "删除成功");
                loadData();
            } else {
                UIUtils.showError(this, "删除失败");
            }
        }
    }
}