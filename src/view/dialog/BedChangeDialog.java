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
        setSize(450, 200);
        setLocationRelativeTo(owner);
        initUI();
        setVisible(true);
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
        if (freeBeds.isEmpty()) {
            newBedCombo.addItem("无空闲床位");
            newBedCombo.setEnabled(false);
        } else {
            for (Bed b : freeBeds) {
                newBedCombo.addItem(b.getRoomNo() + "号房间 " + b.getBedNo() + " (ID:" + b.getId() + ")");
            }
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
        if (selected == null || selected.contains("无空闲床位")) {
            UIUtils.showError(this, "没有可用的空闲床位");
            return;
        }
        // 解析床位ID（格式如 "101号房间 A床 (ID:5)"）
        int start = selected.indexOf("ID:") + 3;
        int end = selected.lastIndexOf(")");
        if (start < 0 || end < 0) {
            UIUtils.showError(this, "床位信息解析错误");
            return;
        }
        int newBedId = Integer.parseInt(selected.substring(start, end));
        boolean success = bedService.changeBed(customerId, newBedId);
        if (success) {
            UIUtils.showInfo(this, "调换成功");
            dispose();
        } else {
            UIUtils.showError(this, "调换失败，请检查新床位状态或数据库事务");
        }
    }
}