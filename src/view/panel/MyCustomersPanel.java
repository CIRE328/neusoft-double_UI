package view.panel;

import pojo.Customer;
import service.CustomerService;
import service.HousekeeperService;
import view.util.TableUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyCustomersPanel extends JPanel {
    private HousekeeperService housekeeperService = new HousekeeperService();
    private CustomerService customerService = new CustomerService();
    private Integer housekeeperId;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public MyCustomersPanel(Integer housekeeperId) {
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
        JButton refreshBtn = new JButton("刷新");
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);
        add(searchPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "姓名", "年龄", "性别", "联系电话", "房间号", "护理级别"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 禁止编辑所有单元格
            }
        };
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> search());
        refreshBtn.addActionListener(e -> loadData());
    }

    // 其余方法保持不变...
    private void loadData() {
        loadData(null);
    }

    private void search() {
        String keyword = searchField.getText().trim();
        loadData(keyword.isEmpty() ? null : keyword);
    }

    private void loadData(String keyword) {
        tableModel.setRowCount(0);
        List<Customer> customers = housekeeperService.findCustomersByHousekeeper(housekeeperId);
        if (keyword != null) {
            customers = customers.stream()
                    .filter(c -> c.getCustomerName().contains(keyword))
                    .toList();
        }
        for (Customer c : customers) {
            String sex = (c.getCustomerSex() != null && c.getCustomerSex() == 1) ? "女" : "男";
            tableModel.addRow(new Object[]{
                    c.getId(), c.getCustomerName(), c.getCustomerAge(), sex,
                    c.getContactTel(), c.getRoomNo(), c.getLevelId()
            });
        }
    }
}