package view.panel;

import pojo.Outward;
import service.CustomerService;
import service.HousekeeperService;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class ApplyOutwardPanel extends JPanel {
    private HousekeeperService housekeeperService = new HousekeeperService();
    private CustomerService customerService = new CustomerService();
    private Integer housekeeperId;
    private JComboBox<String> customerCombo;
    private JTextField reasonField, outgoingTimeField, returnTimeField, escortedField, relationField, telField;

    public ApplyOutwardPanel(Integer housekeeperId) {
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
        addRow("外出事由:", reasonField = new JTextField(20), formPanel, gbc, row++);
        addRow("外出时间 (yyyy-MM-dd):", outgoingTimeField = new JTextField(10), formPanel, gbc, row++);
        addRow("预计返回时间:", returnTimeField = new JTextField(10), formPanel, gbc, row++);
        addRow("陪同人:", escortedField = new JTextField(15), formPanel, gbc, row++);
        addRow("关系:", relationField = new JTextField(10), formPanel, gbc, row++);
        addRow("陪同人电话:", telField = new JTextField(15), formPanel, gbc, row++);

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
            Outward outward = new Outward();
            outward.setCustomerId(customerId);
            outward.setOutgoingreasons(reasonField.getText().trim());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            outward.setOutgoingtime(sdf.parse(outgoingTimeField.getText().trim()));
            outward.setExpectedreturntime(sdf.parse(returnTimeField.getText().trim()));
            outward.setEscorted(escortedField.getText().trim());
            outward.setRelation(relationField.getText().trim());
            outward.setEscortedtel(telField.getText().trim());
            outward.setAuditstatus(0);
            outward.setIsDeleted(0);
            boolean success = customerService.submitOutward(outward);
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
        outgoingTimeField.setText("");
        returnTimeField.setText("");
        escortedField.setText("");
        relationField.setText("");
        telField.setText("");
    }

    private void addRow(String label, Component comp, JPanel panel, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(comp, gbc);
    }
}
