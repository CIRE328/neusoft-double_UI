package view.panel;

import pojo.NurseContent;
import service.NurseService;
import view.component.ButtonColumn;
import view.dialog.NurseItemDialog;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class NurseItemPanel extends JPanel {
    private NurseService nurseService;
    private JTable table;
    private DefaultTableModel tableModel;

    public NurseItemPanel(NurseService nurseService) {
        this.nurseService = nurseService;
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        String[] cols = {"ID", "编号", "名称", "价格", "执行周期", "执行次数", "状态", "编辑", "删除"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 7 || col == 8; }
        };
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        new ButtonColumn(table, "编辑", 7, this::editItem);
        new ButtonColumn(table, "删除", 8, this::deleteItem);

        JButton addBtn = new JButton("新增护理项目");
        addBtn.addActionListener(e -> {
            NurseItemDialog dialog = new NurseItemDialog(SwingUtilities.getWindowAncestor(this), nurseService, null);
            dialog.setVisible(true);
            if (dialog.isSuccess()) loadData();
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(addBtn);
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<NurseContent> list = nurseService.findAllNurseContents();
        for (NurseContent c : list) {
            String status = c.getStatus() == 1 ? "启用" : "停用";
            tableModel.addRow(new Object[]{
                    c.getId(), c.getSerialNumber(), c.getNursingName(),
                    c.getServicePrice(), c.getExecutionCycle(), c.getExecutionTime(),
                    status, "编辑", "删除"
            });
        }
        TableUtils.autoResizeColumns(table);
    }

    private void editItem(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        NurseContent item = nurseService.findAllNurseContents().stream()
                .filter(i -> i.getId().equals(id)).findFirst().orElse(null);
        if (item != null) {
            NurseItemDialog dialog = new NurseItemDialog(SwingUtilities.getWindowAncestor(this), nurseService, item);
            dialog.setVisible(true);
            if (dialog.isSuccess()) loadData();
        }
    }

    private void deleteItem(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        if (UIUtils.confirm(this, "确定删除该护理项目吗？")) {
            boolean success = nurseService.deleteNurseContent(id);
            if (success) loadData();
            else UIUtils.showError(this, "删除失败，可能有关联级别使用");
        }
    }
}
