package view.dialog;

import pojo.User;
import service.UserService;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;

public class UserDialog extends JDialog {
    private UserService userService;
    private User user;
    private boolean success = false;

    private JTextField nicknameField, usernameField, phoneField, emailField;
    private JComboBox<String> sexCombo, roleCombo;

    public UserDialog(Window owner, UserService userService, User user) {
        super(owner, user == null ? "新增用户" : "编辑用户", ModalityType.APPLICATION_MODAL);
        this.userService = userService;
        this.user = user;
        setSize(400, 300);
        setLocationRelativeTo(owner);
        initUI();
        if (user != null) loadData();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow("姓名:", nicknameField = new JTextField(15), gbc, row++);
        addRow("账号:", usernameField = new JTextField(15), gbc, row++);
        addRow("手机号:", phoneField = new JTextField(15), gbc, row++);
        addRow("邮箱:", emailField = new JTextField(15), gbc, row++);
        addRow("性别:", sexCombo = new JComboBox<>(new String[]{"男", "女"}), gbc, row++);
        addRow("角色:", roleCombo = new JComboBox<>(new String[]{"管理员", "健康管家"}), gbc, row++);

        JPanel btnPanel = new JPanel();
        JButton okBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        add(btnPanel, gbc);

        okBtn.addActionListener(e -> save());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void addRow(String label, Component comp, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        add(comp, gbc);
    }

    private void loadData() {
        nicknameField.setText(user.getNickname());
        usernameField.setText(user.getUsername());
        phoneField.setText(user.getPhoneNumber());
        emailField.setText(user.getEmail());
        sexCombo.setSelectedIndex(user.getSex() == 1 ? 0 : 1);
        roleCombo.setSelectedIndex(user.getRoleId() == 1 ? 0 : 1);
    }

    private void save() {
        try {
            if (user == null) user = new User();
            user.setNickname(nicknameField.getText().trim());
            user.setUsername(usernameField.getText().trim());
            user.setPhoneNumber(phoneField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setSex(sexCombo.getSelectedIndex() == 0 ? 1 : 0);
            user.setRoleId(roleCombo.getSelectedIndex() == 0 ? 1 : 2);
            if (user.getId() == null) {
                User added = userService.addUser(user);
                if (added == null) {
                    UIUtils.showError(this, "用户名已存在");
                    return;
                }
            } else {
                userService.updateUser(user);
            }
            success = true;
            dispose();
        } catch (Exception ex) {
            UIUtils.showError(this, "保存失败: " + ex.getMessage());
        }
    }

    public boolean isSuccess() { return success; }
}
