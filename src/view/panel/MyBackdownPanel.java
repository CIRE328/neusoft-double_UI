package view.panel;

import pojo.BackDown;
import pojo.Customer;
import service.CustomerService;
import service.HousekeeperService;
import view.util.TableUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MyBackdownPanel extends JPanel {
    private HousekeeperService housekeeperService = new HousekeeperService();
    private CustomerService customerService = new CustomerService();
    private Integer housekeeperId;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusCombo;

    public MyBackdownPanel(Integer housekeeperId) {
        this.housekeeperId = housekeeperId;
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("审批状态:"));
        statusCombo = new JComboBox<>(new String[]{"全部", "待审核", "已通过", "已拒绝"});
        JButton searchBtn = new JButton("查询");
        filterPanel.add(statusCombo);
        filterPanel.add(searchBtn);
        add(filterPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "客户姓名", "退住类型", "退住原因", "退住时间", "审批状态"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> loadData());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Integer> customerIds = housekeeperService.findCustomersByHousekeeper(housekeeperId)
                .stream().map(Customer::getId).collect(Collectors.toList());
        if (customerIds.isEmpty()) return;

        List<BackDown> backdowns = customerService.findAllBackdowns().stream()
                .filter(b -> customerIds.contains(b.getCustomerId()))
                .collect(Collectors.toList());

        String statusFilter = (String) statusCombo.getSelectedItem();
        for (BackDown b : backdowns) {
            String status;
            if (b.getAuditstatus() == 0) status = "待审核";
            else if (b.getAuditstatus() == 1) status = "已通过";
            else status = "已拒绝";
            if (!"全部".equals(statusFilter) && !status.equals(statusFilter)) continue;

            String customerName = customerService.findCustomerById(b.getCustomerId())
                    .map(Customer::getCustomerName).orElse("未知");
            String type = b.getRetreattype() == 0 ? "正常退住" : (b.getRetreattype() == 1 ? "死亡退住" : "保留床位");
            tableModel.addRow(new Object[]{
                    b.getId(), customerName, type, b.getRetreatmentreason(),
                    b.getRetreatment(), status
            });
        }
        TableUtils.autoResizeColumns(table);
    }
}