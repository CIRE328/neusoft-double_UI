package view.panel;

import pojo.Customer;
import pojo.Outward;
import service.CustomerService;
import service.HousekeeperService;
import view.component.ButtonColumn;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MyOutwardPanel extends JPanel {
    private HousekeeperService housekeeperService = new HousekeeperService();
    private CustomerService customerService = new CustomerService();
    private Integer housekeeperId;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusCombo;

    public MyOutwardPanel(Integer housekeeperId) {
        this.housekeeperId = housekeeperId;
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        // 状态筛选
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("审批状态:"));
        statusCombo = new JComboBox<>(new String[]{"全部", "待审核", "已通过", "已拒绝"});
        JButton searchBtn = new JButton("查询");
        filterPanel.add(statusCombo);
        filterPanel.add(searchBtn);
        add(filterPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "客户姓名", "外出事由", "外出时间", "预计返回", "实际返回", "审批状态", "操作"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 7; }
        };
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        new ButtonColumn(table, "回院登记", 7, this::returnFromOutward);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> loadData());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        // 获取当前管家服务的客户ID列表
        List<Integer> customerIds = housekeeperService.findCustomersByHousekeeper(housekeeperId)
                .stream().map(Customer::getId).collect(Collectors.toList());
        if (customerIds.isEmpty()) return;

        List<Outward> outwards = customerService.findAllOutwards().stream()
                .filter(o -> customerIds.contains(o.getCustomerId()))
                .collect(Collectors.toList());

        String statusFilter = (String) statusCombo.getSelectedItem();
        for (Outward o : outwards) {
            String status;
            if (o.getAuditstatus() == 0) status = "待审核";
            else if (o.getAuditstatus() == 1) status = "已通过";
            else status = "已拒绝";
            if (!"全部".equals(statusFilter) && !status.equals(statusFilter)) continue;

            String customerName = customerService.findCustomerById(o.getCustomerId())
                    .map(Customer::getCustomerName).orElse("未知");
            String actualReturn = o.getActualreturntime() == null ? "未返回" :
                    new SimpleDateFormat("yyyy-MM-dd").format(o.getActualreturntime());
            tableModel.addRow(new Object[]{
                    o.getId(), customerName, o.getOutgoingreasons(),
                    o.getOutgoingtime(), o.getExpectedreturntime(), actualReturn, status,
                    "回院登记"
            });
        }
        TableUtils.autoResizeColumns(table);
    }

    private void returnFromOutward(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer outwardId = (Integer) tableModel.getValueAt(row, 0);
        String status = (String) tableModel.getValueAt(row, 6);
        if (!"已通过".equals(status)) {
            UIUtils.showError(this, "只有审核通过的申请才能登记回院");
            return;
        }
        Date actualReturn = new Date();
        boolean success = customerService.returnFromOutward(outwardId, actualReturn);
        if (success) {
            UIUtils.showInfo(this, "回院登记成功");
            loadData();
        } else {
            UIUtils.showError(this, "登记失败");
        }
    }
}
