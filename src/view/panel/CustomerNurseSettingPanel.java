package view.panel;

import pojo.Customer;
import service.CustomerService;
import service.NurseService;
import view.dialog.CustomerNurseSettingDialog;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 客户护理设置面板
 * 列出客户并打开护理设置对话框，配置护理级别与护理项目
 */

public class CustomerNurseSettingPanel extends JPanel {
    private NurseService nurseService;
    private CustomerService customerService = new CustomerService();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    /**
     * 构造函数
     *
     * @param nurseService 护理服务
     */

    public CustomerNurseSettingPanel(NurseService nurseService) {
        this.nurseService = nurseService;
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

        // 表格
        String[] cols = {"ID", "姓名", "年龄", "性别", "联系电话", "护理级别ID", "操作"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1 && e.getClickCount() == 2) {
                    Integer customerId = (Integer) tableModel.getValueAt(row, 0);
                    showSettingDialog(customerId);
                }
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 按钮
        JButton settingBtn = new JButton("护理设置");
        settingBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                UIUtils.showError(this, "请先选择客户");
                return;
            }
            Integer customerId = (Integer) tableModel.getValueAt(row, 0);
            showSettingDialog(customerId);
        });
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(settingBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> search());
        refreshBtn.addActionListener(e -> loadData());
    }

    private void search() {
        String kw = searchField.getText().trim();
        loadData(kw.isEmpty() ? null : kw);
    }

    private void loadData() {
        loadData(null);
    }

    private void loadData(String keyword) {
        tableModel.setRowCount(0);
        List<Customer> list = (keyword == null) ? customerService.findAllCustomers() : customerService.findCustomersByName(keyword);
        for (Customer c : list) {
            String sex = (c.getCustomerSex() != null && c.getCustomerSex() == 1) ? "女" : "男";
            tableModel.addRow(new Object[]{
                    c.getId(), c.getCustomerName(), c.getCustomerAge(), sex,
                    c.getContactTel(), c.getLevelId(), "双击设置"
            });
        }
        TableUtils.autoResizeColumns(table);
    }

    private void showSettingDialog(Integer customerId) {
        CustomerNurseSettingDialog dialog = new CustomerNurseSettingDialog(SwingUtilities.getWindowAncestor(this), nurseService, customerId);
        dialog.setVisible(true);
        loadData(); // 刷新级别显示
    }
}
