package view.panel;

import pojo.NurseContent;
import pojo.NurseRecord;
import service.NurseService;
import view.component.ButtonColumn;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * 护理记录面板
 * 健康管家查看并管理自己提交的护理记录，支持删除
 */

public class NurseRecordPanel extends JPanel {
    private NurseService nurseService = new NurseService();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private Integer housekeeperId;

    /**
     * 构造函数
     *
     * @param housekeeperId 当前健康管家 ID
     */

    public NurseRecordPanel(Integer housekeeperId) {
        this.housekeeperId = housekeeperId;
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("客户姓名:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("查询");
        searchBtn.addActionListener(e -> {
            // 根据客户姓名查询（需要实现，此处简化，可调用 service 按客户姓名搜索）
            loadData();
        });
        searchPanel.add(searchBtn);
        add(searchPanel, BorderLayout.NORTH);

        // 表格列增加“删除”操作列
        String[] cols = {"ID", "客户ID", "护理项目", "护理时间", "护理次数", "护理人员", "操作"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 6; }
        };
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        new ButtonColumn(table, "删除", 6, this::deleteRecord);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        if (housekeeperId == null) return;
        List<NurseRecord> records = nurseService.getNurseRecordsByHousekeeper(housekeeperId);
        for (NurseRecord r : records) {
            String itemName = nurseService.findAllNurseContents().stream()
                    .filter(c -> c.getId().equals(r.getItemId()))
                    .findFirst().map(NurseContent::getNursingName).orElse("");
            tableModel.addRow(new Object[]{
                    r.getId(), r.getCustomerId(), itemName, r.getNursingTime(),
                    r.getNursingCount(), r.getUserId(), "删除"
            });
        }
        TableUtils.autoResizeColumns(table);
    }

    private void deleteRecord(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer recordId = (Integer) tableModel.getValueAt(row, 0);
        if (UIUtils.confirm(this, "确定删除该护理记录吗？")) {
            boolean success = nurseService.deleteNurseRecord(recordId);
            if (success) {
                UIUtils.showInfo(this, "删除成功");
                loadData();
            } else {
                UIUtils.showError(this, "删除失败");
            }
        }
    }
}