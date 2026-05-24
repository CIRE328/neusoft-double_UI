package view.dialog;

import pojo.User;
import service.HousekeeperService;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AssignHousekeeperDialog extends JDialog {
    private HousekeeperService housekeeperService;
    private Integer customerId;
    private boolean success = false;
    private JComboBox<String> housekeeperCombo;
    private List<User> housekeepers;

    public AssignHousekeeperDialog(Window owner, HousekeeperService housekeeperService, Integer customerId) {
        super(owner, "分配管家", ModalityType.APPLICATION_MODAL);
        this.housekeeperService = housekeeperService;
        this.customerId = customerId;
        setSize(350, 150);
        setLocationRelativeTo(owner);
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(3, 2, 10, 10));
        add(new JLabel("选择管家:"));
        housekeepers = housekeeperService.findAllHousekeepers();
        String[] names = housekeepers.stream().map(u -> u.getId() + " - " + u.getNickname()).toArray(String[]::new);
        housekeeperCombo = new JComboBox<>(names);
        add(housekeeperCombo);
        JButton okBtn = new JButton("确定");
        JButton cancelBtn = new JButton("取消");
        add(okBtn);
        add(cancelBtn);
        okBtn.addActionListener(e -> assign());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void assign() {
        int index = housekeeperCombo.getSelectedIndex();
        if (index < 0) return;
        Integer housekeeperId = housekeepers.get(index).getId();
        boolean ok = housekeeperService.assignHousekeeper(customerId, housekeeperId);
        if (ok) {
            success = true;
            dispose();
        } else {
            UIUtils.showError(this, "分配失败");
        }
    }

    public boolean isSuccess() { return success; }
}