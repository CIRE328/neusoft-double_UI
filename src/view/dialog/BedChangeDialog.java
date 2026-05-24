package view.dialog;

import pojo.Bed;
import service.BedService;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BedChangeDialog extends JDialog {
    private BedService bedService = new BedService();
    private Integer customerId;
    private Integer oldBedId;
    private JComboBox<String> newBedCombo;

    public BedChangeDialog(Window owner, Integer customerId, Integer oldBedId) {
        super(owner, "床位调换", ModalityType.APPLICATION_MODAL);
        this.customerId = customerId;
        this.oldBedId = oldBedId;
        setSize(400, 180);
        setLocationRelativeTo(owner);
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("选择新床位："), gbc);
        newBedCombo = new JComboBox<>();
        gbc.gridx = 1;
        add(newBedCombo, gbc);
        // 加载空闲床位（排除当前床位）
        List<Bed> freeBeds = bedService.getAllBeds().stream()
                .filter(b -> b.getBedStatus() == 1 && !b.getId().equals(oldBedId))
                .toList();
        for (Bed b : freeBeds) {
            newBedCombo.addItem(b.getRoomNo() + "号房间 " + b.getBedNo() + " (ID:" + b.getId() + ")");
        }

        JButton okBtn = new JButton("确定");
        JButton cancelBtn = new JButton("取消");
        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, gbc);

        okBtn.addActionListener(e -> change());
        cancelBtn.addActionListener(e -> dispose());
        pack();
    }

    private void change() {
        String selected = (String) newBedCombo.getSelectedItem();
        if (selected == null) return;
        int newBedId = Integer.parseInt(selected.substring(selected.indexOf("ID:") + 3, selected.length() - 1));
        boolean success = bedService.changeBed(customerId, newBedId);
        if (success) {
            UIUtils.showInfo(this, "调换成功");
            dispose();
        } else {
            UIUtils.showError(this, "调换失败，请检查新床位状态");
        }
    }
}
