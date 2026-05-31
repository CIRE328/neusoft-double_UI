package view.dialog;

import pojo.NurseContent;
import service.NurseService;
import view.component.ButtonColumn;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

public class NurseLevelItemDialog extends JDialog {
    private NurseService nurseService;
    private Integer levelId;
    private JTable assignedTable, availableTable;
    private DefaultTableModel assignedModel, availableModel;

    public NurseLevelItemDialog(Window owner, NurseService nurseService, Integer levelId) {
        super(owner, "配置护理级别项目", ModalityType.APPLICATION_MODAL);
        this.nurseService = nurseService;
        this.levelId = levelId;
        setSize(800, 500);
        setLocationRelativeTo(owner);
        initUI();
        loadData();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new GridLayout(1, 2, 10, 10));
        // 左边：已配置项目
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("已配置项目"));
        String[] cols1 = {"ID", "名称", "操作"};
        assignedModel = new DefaultTableModel(cols1, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 2; }
        };
        assignedTable = new JTable(assignedModel);
        TableUtils.styleTable(assignedTable);
        new ButtonColumn(assignedTable, "移除", 2, this::removeItem);
        leftPanel.add(new JScrollPane(assignedTable), BorderLayout.CENTER);

        // 右边：可用项目（未配置的启用项目）
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("可用护理项目"));
        String[] cols2 = {"ID", "名称", "操作"};
        availableModel = new DefaultTableModel(cols2, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 2; }
        };
        availableTable = new JTable(availableModel);
        TableUtils.styleTable(availableTable);
        new ButtonColumn(availableTable, "添加", 2, this::addItem);
        rightPanel.add(new JScrollPane(availableTable), BorderLayout.CENTER);

        add(leftPanel);
        add(rightPanel);
    }

    private void loadData() {
        // 已配置
        assignedModel.setRowCount(0);
        List<NurseContent> assigned = nurseService.getItemsByLevelId(levelId);
        for (NurseContent c : assigned) {
            assignedModel.addRow(new Object[]{c.getId(), c.getNursingName(), "移除"});
        }
        // 可用：所有启用且未配置的护理项目
        List<NurseContent> allEnabled = nurseService.findNurseContentsByStatus(1);
        List<Integer> assignedIds = assigned.stream().map(NurseContent::getId).collect(Collectors.toList());
        List<NurseContent> available = allEnabled.stream()
                .filter(c -> !assignedIds.contains(c.getId()))
                .collect(Collectors.toList());
        availableModel.setRowCount(0);
        for (NurseContent c : available) {
            availableModel.addRow(new Object[]{c.getId(), c.getNursingName(), "添加"});
        }
    }

    private void addItem(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer itemId = (Integer) availableModel.getValueAt(row, 0);
        boolean success = nurseService.addItemToLevel(levelId, itemId);
        if (success) loadData();
        else UIUtils.showError(this, "添加失败，可能已存在");
    }

    private void removeItem(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer itemId = (Integer) assignedModel.getValueAt(row, 0);
        boolean success = nurseService.removeItemFromLevel(levelId, itemId);
        if (success) loadData();
        else UIUtils.showError(this, "移除失败");
    }
}
