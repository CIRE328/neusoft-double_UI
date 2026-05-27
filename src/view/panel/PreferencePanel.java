package view.panel;

import pojo.Customer;
import pojo.Preference;
import service.CustomerService;
import service.MealService;
import view.component.ButtonColumn;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Optional;

public class PreferencePanel extends JPanel {
    private MealService mealService;
    private CustomerService customerService = new CustomerService();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public PreferencePanel(MealService mealService) {
        this.mealService = mealService;
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

        // 表格（增加删除列）
        String[] cols = {"客户ID", "客户姓名", "饮食喜好", "注意事项", "编辑", "删除"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4 || col == 5;
            }
        };
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        new ButtonColumn(table, "编辑", 4, this::editPreference);
        new ButtonColumn(table, "删除", 5, this::deletePreference);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> search());
        refreshBtn.addActionListener(e -> loadData());
    }

    private void loadData() {
        loadData(null);
    }

    private void search() {
        String kw = searchField.getText().trim();
        loadData(kw.isEmpty() ? null : kw);
    }

    private void loadData(String keyword) {
        tableModel.setRowCount(0);
        List<Customer> customers = customerService.findAllCustomers();
        if (keyword != null) {
            customers = customers.stream()
                    .filter(c -> c.getCustomerName() != null && c.getCustomerName().contains(keyword))
                    .toList();
        }
        for (Customer c : customers) {
            Optional<Preference> prefOpt = mealService.findPreferenceByCustomerId(c.getId());
            String preferences = prefOpt.map(Preference::getPreferences).orElse("");
            String attention = prefOpt.map(Preference::getAttention).orElse("");
            tableModel.addRow(new Object[]{
                    c.getId(), c.getCustomerName(), preferences, attention, "编辑", "删除"
            });
        }
        TableUtils.autoResizeColumns(table);
    }

    private void editPreference(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer customerId = (Integer) tableModel.getValueAt(row, 0);
        String currentPref = (String) tableModel.getValueAt(row, 2);
        String currentAtt = (String) tableModel.getValueAt(row, 3);
        String preferences = JOptionPane.showInputDialog(this, "饮食喜好:", currentPref);
        if (preferences != null) {
            String attention = JOptionPane.showInputDialog(this, "注意事项:", currentAtt);
            Preference cp = new Preference();
            cp.setCustomerId(customerId);
            cp.setPreferences(preferences);
            cp.setAttention(attention == null ? "" : attention);
            boolean success = mealService.savePreference(cp);
            if (success) {
                UIUtils.showInfo(this, "保存成功");
                loadData();
            } else {
                UIUtils.showError(this, "保存失败");
            }
        }
    }

    private void deletePreference(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer customerId = (Integer) tableModel.getValueAt(row, 0);
        if (UIUtils.confirm(this, "确定删除该客户的饮食喜好记录吗？")) {
            boolean success = mealService.deletePreference(customerId);
            if (success) {
                UIUtils.showInfo(this, "删除成功");
                loadData();
            } else {
                UIUtils.showError(this, "删除失败");
            }
        }
    }
}