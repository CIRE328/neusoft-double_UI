package view.panel;

import pojo.Customer;
import service.BedService;
import service.CustomerService;
import view.component.ButtonColumn;
import view.dialog.*;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CustomerPanel extends JPanel {
    private CustomerService customerService = new CustomerService();
    private BedService bedService = new BedService();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public CustomerPanel() {
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        // 顶部查询栏
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        searchPanel.add(new JLabel("客户姓名："));
        searchField = new JTextField(15);
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("查询");
        JButton refreshBtn = new JButton("刷新");
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);
        add(searchPanel, BorderLayout.NORTH);

        // 表格列（增加操作列）
        String[] columns = {"ID", "姓名", "年龄", "性别", "身份证", "联系电话", "房间号", "床位ID", "入住时间", "合同到期", "护理级别", "操作"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 11; // 操作列可编辑
            }
        };
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        // 添加操作按钮列（编辑和删除放在一起或分开，这里用一个按钮打开操作菜单）
        new ButtonColumn(table, "操作", 11, this::onAction);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 底部按钮栏
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton registerBtn = new JButton("入住登记");
        JButton modifyBtn = new JButton("修改客户");
        JButton deleteBtn = new JButton("删除客户");
        JButton outwardAuditBtn = new JButton("外出审核");
        JButton backdownAuditBtn = new JButton("退住审核");
        btnPanel.add(registerBtn);
        btnPanel.add(modifyBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(outwardAuditBtn);
        btnPanel.add(backdownAuditBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // 事件绑定
        searchBtn.addActionListener(e -> search());
        refreshBtn.addActionListener(e -> loadData());
        registerBtn.addActionListener(e -> showRegisterDialog());
        modifyBtn.addActionListener(e -> modifyCustomer());
        deleteBtn.addActionListener(e -> deleteCustomer());
        outwardAuditBtn.addActionListener(e -> auditOutward());
        backdownAuditBtn.addActionListener(e -> auditBackdown());
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
        List<Customer> list = (keyword == null) ? customerService.findAllCustomers() : customerService.findCustomersByName(keyword);
        for (Customer c : list) {
            String sex = (c.getCustomerSex() != null && c.getCustomerSex() == 1) ? "女" : "男";
            tableModel.addRow(new Object[]{
                    c.getId(), c.getCustomerName(), c.getCustomerAge(), sex,
                    c.getIdcard(), c.getContactTel(), c.getRoomNo(), c.getBedId(),
                    c.getCheckinDate(), c.getExpirationDate(), c.getLevelId(), "操作"
            });
        }
        TableUtils.autoResizeColumns(table);
    }

    private void onAction(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        // 弹出操作菜单：编辑、删除
        JPopupMenu popup = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("编辑");
        JMenuItem deleteItem = new JMenuItem("删除");
        popup.add(editItem);
        popup.add(deleteItem);
        editItem.addActionListener(ev -> showModifyDialog(id));
        deleteItem.addActionListener(ev -> deleteCustomerById(id));
        Component invoker = (Component) e.getSource();
        popup.show(invoker, invoker.getMousePosition().x, invoker.getMousePosition().y);
    }

    private void showRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(SwingUtilities.getWindowAncestor(this), bedService);
        dialog.setVisible(true);
        if (dialog.isSuccess()) loadData();
    }

    private void modifyCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            UIUtils.showError(this, "请先选择要修改的客户");
            return;
        }
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        showModifyDialog(id);
    }

    private void showModifyDialog(Integer customerId) {
        ModifyCustomerDialog dialog = new ModifyCustomerDialog(SwingUtilities.getWindowAncestor(this), customerId);
        dialog.setVisible(true);
        if (dialog.isSuccess()) loadData();
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            UIUtils.showError(this, "请先选择要删除的客户");
            return;
        }
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        deleteCustomerById(id);
    }

    private void deleteCustomerById(Integer id) {
        if (UIUtils.confirm(this, "确定删除客户吗？")) {
            boolean success = customerService.deleteCustomer(id);
            if (success) {
                loadData();
                UIUtils.showInfo(this, "删除成功");
            } else {
                UIUtils.showError(this, "删除失败");
            }
        }
    }

    private void auditOutward() {
        AuditOutwardDialog dialog = new AuditOutwardDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        loadData();
    }

    private void auditBackdown() {
        AuditBackdownDialog dialog = new AuditBackdownDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        loadData();
    }
}
