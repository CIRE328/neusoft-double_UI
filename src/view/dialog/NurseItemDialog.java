package view.dialog;

import pojo.NurseContent;
import service.NurseService;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;

public class NurseItemDialog extends JDialog {
    private NurseService nurseService;
    private NurseContent item;
    private boolean success = false;

    private JTextField serialField, nameField, priceField, cycleField, timesField, messageField;
    private JComboBox<String> statusCombo;

    public NurseItemDialog(Window owner, NurseService nurseService, NurseContent item) {
        super(owner, item == null ? "新增护理项目" : "编辑护理项目", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.nurseService = nurseService;
        this.item = item;
        setSize(400, 350);
        setLocationRelativeTo(owner);
        initUI();
        if (item != null) loadData();
    }

    private void initUI() {
        setLayout(new GridLayout(8, 2, 10, 10));
        add(new JLabel("编号:"));
        serialField = new JTextField();
        add(serialField);
        add(new JLabel("名称:"));
        nameField = new JTextField();
        add(nameField);
        add(new JLabel("价格:"));
        priceField = new JTextField();
        add(priceField);
        add(new JLabel("执行周期:"));
        cycleField = new JTextField();
        add(cycleField);
        add(new JLabel("执行次数:"));
        timesField = new JTextField();
        add(timesField);
        add(new JLabel("描述:"));
        messageField = new JTextField();
        add(messageField);
        add(new JLabel("状态:"));
        statusCombo = new JComboBox<>(new String[]{"启用", "停用"});
        add(statusCombo);
        JButton okBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        add(okBtn);
        add(cancelBtn);
        okBtn.addActionListener(e -> save());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void loadData() {
        serialField.setText(item.getSerialNumber());
        nameField.setText(item.getNursingName());
        priceField.setText(item.getServicePrice());
        cycleField.setText(item.getExecutionCycle());
        timesField.setText(item.getExecutionTime());
        messageField.setText(item.getMessage());
        statusCombo.setSelectedIndex(item.getStatus() == 1 ? 0 : 1);
    }

    private void save() {
        try {
            if (item == null) item = new NurseContent();
            item.setSerialNumber(serialField.getText().trim());
            item.setNursingName(nameField.getText().trim());
            item.setServicePrice(priceField.getText().trim());
            item.setExecutionCycle(cycleField.getText().trim());
            item.setExecutionTime(timesField.getText().trim());
            item.setMessage(messageField.getText().trim());
            item.setStatus(statusCombo.getSelectedIndex() == 0 ? 1 : 2);
            if (item.getId() == null) {
                nurseService.addNurseContent(item);
            } else {
                nurseService.updateNurseContent(item);
            }
            success = true;
            dispose();
        } catch (Exception ex) {
            UIUtils.showError(this, "保存失败: " + ex.getMessage());
        }
    }

    public boolean isSuccess() { return success; }
}