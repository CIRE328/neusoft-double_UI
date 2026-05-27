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
import java.util.stream.Collectors;

public class CustomerPanel extends JPanel {
    private CustomerService customerService = new CustomerService();
    private BedService bedService = new BedService();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> typeCombo;

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

        // 老人类型筛选
        searchPanel.add(new JLabel("  老人类型："));
        typeCombo = new JComboBox<>(new String[]{"全部", "自理老人", "护理老人"});
        searchPanel.add(typeCombo);

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
        refreshBtn.addActionListener(e -> refresh());
        registerBtn.addActionListener(e -> showRegisterDialog());
        modifyBtn.addActionListener(e -> modifyCustomer());
        deleteBtn.addActionListener(e -> deleteCustomer());
        outwardAuditBtn.addActionListener(e -> auditOutward());
        backdownAuditBtn.addActionListener(e -> auditBackdown());
    }

    private void loadData() {
        loadData(null, "全部");
    }

    private void search() {
        String kw = searchField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        loadData(kw.isEmpty() ? null : kw, type);
    }

    private void refresh() {
        searchField.setText("");
        typeCombo.setSelectedIndex(0);
        loadData(null, "全部");
    }

    private void loadData(String keyword, String type) {
        tableModel.setRowCount(0);
        List<Customer> list;
        if (keyword == null) {
            list = customerService.findAllCustomers();
        } else {
            list = customerService.findCustomersByName(keyword);
        }
        // 根据老人类型筛选（调用 Service 方法）
        if ("自理老人".equals(type)) {
            list = customerService.findCustomersByType("自理老人");
            // 如果上面方法返回全部后过滤，也可以直接调用 Service
        } else if ("护理老人".equals(type)) {
            list = customerService.findCustomersByType("护理老人");
        }
        // 注意：Service 的 findCustomersByType 已经返回筛选后的列表，但为了同时支持姓名过滤，
        // 如果姓名关键字存在，需要在筛选结果上再次过滤（因为 Service 方法没有组合查询）
        if (keyword != null && !keyword.isEmpty()) {
            list = list.stream()
                    .filter(c -> c.getCustomerName() != null && c.getCustomerName().contains(keyword))
                    .collect(Collectors.toList());
        }
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
        if (dialog.isSuccess()) refresh();
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
        if (dialog.isSuccess()) refresh();
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
                UIUtils.showInfo(this, "删除成功");
                refresh();
            } else {
                UIUtils.showError(this, "删除失败");
            }
        }
    }

    private void auditOutward() {
        // 强制停止表格编辑，避免事件冲突
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        SwingUtilities.invokeLater(() -> {
            AuditOutwardDialog dialog = new AuditOutwardDialog(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            loadData(); // 刷新
        });
    }

    private void auditBackdown() {
        // 强制停止表格编辑，避免事件冲突
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        SwingUtilities.invokeLater(() -> {
            AuditBackdownDialog dialog = new AuditBackdownDialog(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            loadData(); // 刷新
        });
    }

}