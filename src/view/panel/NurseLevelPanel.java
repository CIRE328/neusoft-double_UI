package view.panel;

import pojo.NurseLevel;
import service.NurseService;
import view.component.ButtonColumn;
import view.dialog.NurseLevelDialog;
import view.dialog.NurseLevelItemDialog;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class NurseLevelPanel extends JPanel {
    private NurseService nurseService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusCombo;

    public NurseLevelPanel(NurseService nurseService) {
        this.nurseService = nurseService;
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        // 顶部筛选栏
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("状态:"));
        statusCombo = new JComboBox<>(new String[]{"全部", "启用", "停用"});
        filterPanel.add(statusCombo);
        JButton searchBtn = new JButton("查询");
        JButton refreshBtn = new JButton("刷新");
        filterPanel.add(searchBtn);
        filterPanel.add(refreshBtn);
        add(filterPanel, BorderLayout.NORTH);

        // 表格列（共6列，索引0-5，操作列在3,4,5）
        String[] cols = {"ID", "级别名称", "状态", "配置项目", "编辑", "删除"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 3 || col == 4 || col == 5; }
        };
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        new ButtonColumn(table, "配置", 3, this::configItems);
        new ButtonColumn(table, "编辑", 4, this::editLevel);
        new ButtonColumn(table, "删除", 5, this::deleteLevel);

        JButton addBtn = new JButton("新增护理级别");
        addBtn.addActionListener(e -> {
            NurseLevelDialog dialog = new NurseLevelDialog(SwingUtilities.getWindowAncestor(this), nurseService, null);
            dialog.setVisible(true);
            if (dialog.isSuccess()) loadData();
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(addBtn);
        // 将新增按钮放在筛选栏右侧
        filterPanel.add(Box.createHorizontalStrut(30));
        filterPanel.add(addBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> loadData());
        refreshBtn.addActionListener(e -> {
            statusCombo.setSelectedIndex(0);
            loadData();
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        String selected = (String) statusCombo.getSelectedItem();
        List<NurseLevel> list;
        if ("启用".equals(selected)) {
            list = nurseService.findNurseLevelsByStatus(1);
        } else if ("停用".equals(selected)) {
            list = nurseService.findNurseLevelsByStatus(2);
        } else {
            list = nurseService.findAllNurseLevels();
        }
        for (NurseLevel l : list) {
            String status = l.getLevelStatus() == 1 ? "启用" : "停用";
            tableModel.addRow(new Object[]{
                    l.getId(), l.getLevelName(), status, "配置", "编辑", "删除"
            });
        }
        TableUtils.autoResizeColumns(table);
    }

    private void configItems(ActionEvent e) {
        // 停止当前表格编辑
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        int row = Integer.parseInt(e.getActionCommand());
        Integer levelId = (Integer) tableModel.getValueAt(row, 0);
        SwingUtilities.invokeLater(() -> {
            NurseLevelItemDialog dialog = new NurseLevelItemDialog(SwingUtilities.getWindowAncestor(this), nurseService, levelId);
            dialog.setVisible(true);
            loadData();
        });
    }

    private void editLevel(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        NurseLevel level = nurseService.findAllNurseLevels().stream()
                .filter(l -> l.getId().equals(id)).findFirst().orElse(null);
        if (level != null) {
            NurseLevelDialog dialog = new NurseLevelDialog(SwingUtilities.getWindowAncestor(this), nurseService, level);
            dialog.setVisible(true);
            if (dialog.isSuccess()) loadData();
        }
    }

    private void deleteLevel(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        if (UIUtils.confirm(this, "确定删除护理级别吗？会同时删除级别-项目关联。")) {
            boolean success = nurseService.deleteNurseLevel(id);
            if (success) loadData();
            else UIUtils.showError(this, "删除失败");
        }
    }
}