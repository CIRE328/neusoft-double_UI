package view.dialog;

import pojo.Customer;
import service.CustomerService;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class ModifyCustomerDialog extends JDialog {
    private CustomerService customerService = new CustomerService();
    private Integer customerId;
    private boolean success = false;

    private JTextField nameField, phoneField, expireField;

    public ModifyCustomerDialog(Window owner, Integer customerId) {
        super(owner, "修改客户信息", ModalityType.APPLICATION_MODAL);
        this.customerId = customerId;
        setSize(350, 250);
        setLocationRelativeTo(owner);
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new GridLayout(4, 2, 10, 10));
        add(new JLabel("姓名:"));
        nameField = new JTextField();
        add(nameField);
        add(new JLabel("联系电话:"));
        phoneField = new JTextField();
        add(phoneField);
        add(new JLabel("合同到期时间(YYYY-MM-DD):"));
        expireField = new JTextField();
        add(expireField);
        JButton okBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        add(okBtn);
        add(cancelBtn);
        okBtn.addActionListener(e -> save());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void loadData() {
        Customer c = customerService.findCustomerById(customerId).orElse(null);
        if (c == null) {
            UIUtils.showError(this, "客户不存在");
            dispose();
            return;
        }
        nameField.setText(c.getCustomerName());
        phoneField.setText(c.getContactTel());
        if (c.getExpirationDate() != null) {
            expireField.setText(new SimpleDateFormat("yyyy-MM-dd").format(c.getExpirationDate()));
        }
    }

    private void save() {
        try {
            Customer c = customerService.findCustomerById(customerId).orElse(null);
            if (c == null) return;
            c.setCustomerName(nameField.getText().trim());
            c.setContactTel(phoneField.getText().trim());
            if (!expireField.getText().trim().isEmpty()) {
                c.setExpirationDate(new SimpleDateFormat("yyyy-MM-dd").parse(expireField.getText().trim()));
            }
            boolean ok = customerService.updateCustomer(c);
            if (ok) {
                success = true;
                dispose();
                UIUtils.showInfo(this, "修改成功");
            } else {
                UIUtils.showError(this, "修改失败");
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "日期格式错误");
        }
    }

    public boolean isSuccess() { return success; }
}
