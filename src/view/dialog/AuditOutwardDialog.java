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

    public AuditOutwardDialog(Window owner) {
        super(owner, "外出申请审核", ModalityType.APPLICATION_MODAL);
        setSize(800, 400);
        setLocationRelativeTo(owner);
        initUI();
        loadData();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        String[] cols = {"ID", "客户ID", "外出事由", "外出时间", "预计返回", "审批状态", "操作"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 6; }
        };
        table = new JTable(tableModel);
        // 添加操作按钮列
        new view.component.ButtonColumn(table, "审核", 6, this::audit);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Outward> list = customerService.findAllOutwards();
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
        // 弹出选项：通过/拒绝
        String[] options = {"通过", "拒绝"};
        int choice = JOptionPane.showOptionDialog(this, "请选择审核结果", "审核",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 0) {
            customerService.auditOutward(id, true, "管理员");
        } else if (choice == 1) {
            customerService.auditOutward(id, false, "管理员");
        }
        loadData();
    }
}
