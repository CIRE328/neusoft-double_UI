package view.dialog;

import pojo.User;
import service.UserService;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;

public class AdminRegisterDialog extends JDialog {
    private UserService userService = new UserService();
    private boolean success = false;

    private JTextField nameField, usernameField, phoneField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> sexCombo;

    public AdminRegisterDialog(Window owner) {
        super(owner, "创建管理员账户", ModalityType.APPLICATION_MODAL);
        setSize(400, 350);
        setLocationRelativeTo(owner);
        initUI();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow("姓名:", nameField = new JTextField(15), gbc, row++);
        addRow("账号:", usernameField = new JTextField(15), gbc, row++);
        addRow("密码:", passwordField = new JPasswordField(15), gbc, row++);
        addRow("手机号:", phoneField = new JTextField(15), gbc, row++);
        addRow("邮箱:", emailField = new JTextField(15), gbc, row++);
        addRow("性别:", sexCombo = new JComboBox<>(new String[]{"男", "女"}), gbc, row++);

        JButton okBtn = new JButton("创建");
        JButton cancelBtn = new JButton("取消");
        JPanel btnPanel = new JPanel();
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        add(btnPanel, gbc);

        okBtn.addActionListener(e -> register());
        cancelBtn.addActionListener(e -> System.exit(0));
    }

    private void addRow(String label, Component comp, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        add(comp, gbc);
    }

    private void register() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        int sex = sexCombo.getSelectedIndex() == 0 ? 1 : 0;

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            UIUtils.showError(this, "请填写完整信息");
            return;
        }

        User user = new User();
        user.setNickname(name);
        user.setUsername(username);
        user.setPassword(password);
        user.setPhoneNumber(phone);
        user.setEmail(email);
        user.setSex(sex);
        user.setRoleId(1); // 管理员
        user.setIsDeleted(0);
        User added = userService.addUser(user); // 注意 addUser 中会设置默认密码，这里我们直接传入密码，需要修改 UserService 支持自定义密码
        if (added != null) {
            success = true;
            UIUtils.showInfo(this, "管理员创建成功，请使用账号密码登录");
            dispose();
        } else {
            UIUtils.showError(this, "创建失败，账号可能已存在");
        }
    }

    public boolean isSuccess() { return success; }
}
