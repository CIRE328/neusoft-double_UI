package view.panel;

import pojo.Customer;
import pojo.User;
import service.CustomerService;
import service.HousekeeperService;
import view.component.ButtonColumn;
import view.dialog.AssignHousekeeperDialog;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

public class HousekeeperPanel extends JPanel {
    private HousekeeperService housekeeperService = new HousekeeperService();
    private CustomerService customerService = new CustomerService();
    private JTable customerTable;
    private DefaultTableModel customerModel;
    private JTable housekeeperTable;
    private DefaultTableModel housekeeperModel;
    private JTextField searchField;

    public HousekeeperPanel() {
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        // 查询栏
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        searchPanel.add(new JLabel("客户姓名:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("查询");
        JButton refreshBtn = new JButton("刷新");
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);
        add(searchPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.3);

        // 左侧：健康管家列表
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("健康管家列表"));
        String[] housekeeperCols = {"ID", "姓名", "账号", "手机号"};
        housekeeperModel = new DefaultTableModel(housekeeperCols, 0);
        housekeeperTable = new JTable(housekeeperModel);
        TableUtils.styleTable(housekeeperTable);
        leftPanel.add(new JScrollPane(housekeeperTable), BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        // 右侧：客户列表
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("客户列表"));
        String[] customerCols = {"ID", "姓名", "当前管家ID", "管家姓名", "分配","移除"};
        customerModel = new DefaultTableModel(customerCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4;
            }
        };
        customerTable = new JTable(customerModel);
        TableUtils.styleTable(customerTable);
        // 使用方法引用替代 lambda，避免变量捕获问题
        new ButtonColumn(customerTable, "分配", 4, this::assignHousekeeper);
        new ButtonColumn(customerTable, "移除", 5, this::removeHousekeeper);
        rightPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        // 按钮事件
        searchBtn.addActionListener(e -> search());
        refreshBtn.addActionListener(e -> loadData());
    }

    private void loadData() {
        loadHousekeepers();
        loadCustomers(null);
    }

    private void search() {
        String keyword = searchField.getText().trim();
        loadCustomers(keyword.isEmpty() ? null : keyword);
    }

    private void loadHousekeepers() {
        housekeeperModel.setRowCount(0);
        List<User> housekeepers = housekeeperService.findAllHousekeepers();
        for (User u : housekeepers) {
            housekeeperModel.addRow(new Object[]{u.getId(), u.getNickname(), u.getUsername(), u.getPhoneNumber()});
        }
    }

    private void loadCustomers(String keyword) {
        customerModel.setRowCount(0);
        List<Customer> customers = customerService.findAllCustomers();
        if (keyword != null) {
            customers = customers.stream()
                    .filter(c -> c.getCustomerName() != null && c.getCustomerName().contains(keyword))
                    .collect(Collectors.toList());
        }
        List<User> housekeepers = housekeeperService.findAllHousekeepers();
        for (Customer c : customers) {
            String housekeeperName = "";
            if (c.getUserId() != null && c.getUserId() > 0) {
                housekeeperName = housekeepers.stream()
                        .filter(h -> h.getId().equals(c.getUserId()))
                        .findFirst()
                        .map(User::getNickname)
                        .orElse("");
            }
            customerModel.addRow(new Object[]{
                    c.getId(), c.getCustomerName(),
                    c.getUserId() == null || c.getUserId() == -1 ? "无" : c.getUserId(),
                    housekeeperName.isEmpty() ? "无" : housekeeperName,
                    "分配", "移除"
            });
        }
        TableUtils.autoResizeColumns(customerTable);
    }

    private void assignHousekeeper(ActionEvent e) {
        // 不再传入 customerId
        AssignHousekeeperDialog dialog = new AssignHousekeeperDialog(SwingUtilities.getWindowAncestor(this), housekeeperService);
        dialog.setVisible(true);
        if (dialog.isSuccess()) loadData();
    }

    private void removeHousekeeper(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer customerId = (Integer) customerModel.getValueAt(row, 0);
        if (UIUtils.confirm(this, "确定移除该客户的管家吗？")) {
            boolean success = housekeeperService.removeHousekeeper(customerId);
            if (success) {
                UIUtils.showInfo(this, "移除成功");
                loadData();
            } else {
                UIUtils.showError(this, "移除失败");
            }
        }
    }
}