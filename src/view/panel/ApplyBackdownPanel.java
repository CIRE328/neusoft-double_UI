package view.panel;

import pojo.BackDown;
import service.CustomerService;
import service.HousekeeperService;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class ApplyBackdownPanel extends JPanel {
    private HousekeeperService housekeeperService = new HousekeeperService();
    private CustomerService customerService = new CustomerService();
    private Integer housekeeperId;
    private JComboBox<String> customerCombo, typeCombo;
    private JTextField reasonField, backdownTimeField;

    public ApplyBackdownPanel(Integer housekeeperId) {
        this.housekeeperId = housekeeperId;
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        addRow("选择客户:", customerCombo = new JComboBox<>(), formPanel, gbc, row++);
        addRow("退住类型:", typeCombo = new JComboBox<>(new String[]{"正常退住", "死亡退住", "保留床位"}), formPanel, gbc, row++);
        addRow("退住原因:", reasonField = new JTextField(20), formPanel, gbc, row++);
        addRow("退住时间 (yyyy-MM-dd):", backdownTimeField = new JTextField(10), formPanel, gbc, row++);

        JButton submitBtn = new JButton("提交申请");
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(submitBtn, gbc);

        add(formPanel, BorderLayout.CENTER);
        loadCustomers();

        submitBtn.addActionListener(e -> submit());
    }

    private void loadCustomers() {
        customerCombo.removeAllItems();
        housekeeperService.findCustomersByHousekeeper(housekeeperId)
                .forEach(c -> customerCombo.addItem(c.getId() + " - " + c.getCustomerName()));
    }

    private void submit() {
        try {
            if (customerCombo.getItemCount() == 0) {
                UIUtils.showError(this, "没有服务的客户");
                return;
            }
            Integer customerId = Integer.parseInt(((String) customerCombo.getSelectedItem()).split(" - ")[0]);
            BackDown backdown = new BackDown();
            backdown.setCustomerId(customerId);
            backdown.setRetreattype(typeCombo.getSelectedIndex());
            backdown.setRetreatmentreason(reasonField.getText().trim());
            backdown.setRetreatment(new SimpleDateFormat("yyyy-MM-dd").parse(backdownTimeField.getText().trim()));
            backdown.setAuditstatus(0);
            backdown.setIsDeleted(0);
            boolean success = customerService.submitBackdown(backdown);
            if (success) {
                UIUtils.showInfo(this, "申请已提交");
                clearForm();
            } else {
                UIUtils.showError(this, "提交失败");
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "日期格式错误");
        }
    }

    private void clearForm() {
        reasonField.setText("");
        backdownTimeField.setText("");
    }

    private void addRow(String label, Component comp, JPanel panel, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(comp, gbc);
    }
}