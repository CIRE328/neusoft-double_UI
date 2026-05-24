package view.dialog;

import pojo.BackDown;
import service.CustomerService;
import view.component.ButtonColumn;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class AuditBackdownDialog extends JDialog {
    private CustomerService customerService = new CustomerService();
    private JTable table;
    private DefaultTableModel tableModel;

    public AuditBackdownDialog(Window owner) {
        super(owner, "退住申请审核", ModalityType.APPLICATION_MODAL);
        setSize(800, 400);
        setLocationRelativeTo(owner);
        initUI();
        loadData();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        String[] cols = {"ID", "客户ID", "退住类型", "退住原因", "退住时间", "审批状态", "操作"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 6; }
        };
        table = new JTable(tableModel);
        new ButtonColumn(table, "审核", 6, this::audit);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<BackDown> list = customerService.findAllBackdowns();
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
        loadData();
    }
}
