package view.dialog;

import pojo.Outward;
import service.CustomerService;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class AuditOutwardDialog extends JDialog {
    private CustomerService customerService = new CustomerService();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public AuditOutwardDialog(Window owner) {
        super(owner, "外出申请审核", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(850, 450);
        setLocationRelativeTo(owner);
        initUI();
        loadData(null);
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 搜索栏
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("客户姓名:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("查询");
        JButton refreshBtn = new JButton("刷新");
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);
        add(searchPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "客户ID", "外出事由", "外出时间", "预计返回", "审批状态", "操作"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 6; }
        };
        table = new JTable(tableModel);
        new view.component.ButtonColumn(table, "审核", 6, this::audit);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 关闭按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("关闭");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            loadData(keyword.isEmpty() ? null : keyword);
        });
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadData(null);
        });
    }

    private void loadData(String customerName) {
        tableModel.setRowCount(0);
        List<Outward> list;
        if (customerName == null) {
            list = customerService.findAllOutwards();
        } else {
            // 调用 findOutwardsByCustomerName 方法
            list = customerService.findOutwardsByCustomerName(customerName);
        }
        for (Outward o : list) {
            String status;
            if (o.getAuditstatus() == 0) status = "待审核";
            else if (o.getAuditstatus() == 1) status = "已通过";
            else status = "已拒绝";
            tableModel.addRow(new Object[]{
                    o.getId(), o.getCustomerId(), o.getOutgoingreasons(),
                    o.getOutgoingtime(), o.getExpectedreturntime(), status, "审核"
            });
        }
    }

    private void audit(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        String[] options = {"通过", "拒绝"};
        int choice = JOptionPane.showOptionDialog(this, "请选择审核结果", "审核",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 0) {
            customerService.auditOutward(id, true, "管理员");
        } else if (choice == 1) {
            customerService.auditOutward(id, false, "管理员");
        }
        loadData(null); // 刷新
        searchField.setText("");
    }
}