package view.panel;

import pojo.NurseContent;
import pojo.NurseRecord;
import service.NurseService;
import view.util.TableUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NurseRecordPanel extends JPanel {
    private NurseService nurseService = new NurseService();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public NurseRecordPanel(Integer housekeeperId) {
        setLayout(new BorderLayout());
        initUI();
        loadData(housekeeperId);
    }

    private void initUI() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("客户姓名:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("查询");
        searchBtn.addActionListener(e -> {
            // 实际查询需要根据客户姓名检索，此处简化为加载全部
            loadData(null);
        });
        searchPanel.add(searchBtn);
        add(searchPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "客户ID", "护理项目", "护理时间", "护理次数", "护理人员"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData(Integer housekeeperId) {
        tableModel.setRowCount(0);
        if (housekeeperId == null) return;
        List<NurseRecord> records = nurseService.getNurseRecordsByHousekeeper(housekeeperId);
        for (NurseRecord r : records) {
            String itemName = nurseService.findAllNurseContents().stream()
                    .filter(c -> c.getId().equals(r.getItemId()))
                    .findFirst().map(NurseContent::getNursingName).orElse("");
            tableModel.addRow(new Object[]{
                    r.getId(), r.getCustomerId(), itemName, r.getNursingTime(),
                    r.getNursingCount(), r.getUserId()
            });
        }
    }
}