package view.dialog;

import pojo.BackDown;
import service.CustomerService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * 退住申请审核对话框
 * 管理员查看退住申请列表并进行通过或拒绝审核
 */

public class AuditBackdownDialog extends JDialog {
    private CustomerService customerService = new CustomerService();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    /**
     * 构造函数
     * 初始化审核界面、加载数据并显示对话框
     *
     * @param owner 父窗口
     */

    public AuditBackdownDialog(Window owner) {
        super(owner, "退住申请审核", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(850, 450);
        setLocationRelativeTo(owner);
        initUI();
        loadData(null);
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("客户姓名:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("查询");
        JButton refreshBtn = new JButton("刷新");
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);
        add(searchPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "客户ID", "退住类型", "退住原因", "退住时间", "审批状态", "操作"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 6; }
        };
        table = new JTable(tableModel);
        new view.component.ButtonColumn(table, "审核", 6, this::audit);
        add(new JScrollPane(table), BorderLayout.CENTER);

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
        List<BackDown> list;
        if (customerName == null) {
            list = customerService.findAllBackdowns();
        } else {
            list = customerService.findBackdownsByCustomerName(customerName);
        }
        for (BackDown b : list) {
            String type = b.getRetreattype() == 0 ? "正常退住" : (b.getRetreattype() == 1 ? "死亡退住" : "保留床位");
            String status;
            if (b.getAuditstatus() == 0) status = "待审核";
            else if (b.getAuditstatus() == 1) status = "已通过";
            else status = "已拒绝";
            tableModel.addRow(new Object[]{
                    b.getId(), b.getCustomerId(), type, b.getRetreatmentreason(),
                    b.getRetreatment(), status, "审核"
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
            customerService.auditBackdown(id, true, "管理员");
        } else if (choice == 1) {
            customerService.auditBackdown(id, false, "管理员");
        }
        loadData(null);
        searchField.setText("");
    }
}