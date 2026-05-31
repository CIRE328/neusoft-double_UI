package view.dialog;

import pojo.User;
import service.UserService;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;

/**
 * 用户编辑对话框
 * 用于新增或编辑健康管家用户信息，管理员信息为只读
 */

public class UserDialog extends JDialog {
    private UserService userService;
    private User user;
    private boolean success = false;

    private JTextField nicknameField, usernameField, phoneField, emailField;
    private JComboBox<String> sexCombo, roleCombo;
    private JPasswordField passwordField;   // 新增密码字段
    private JButton okBtn;

    /**
     * 构造函数
     *
     * @param owner       父窗口
     * @param userService 用户服务
     * @param user        待编辑的用户，为 null 时表示新增健康管家
     */

    public UserDialog(Window owner, UserService userService, User user) {
        super(owner, user == null ? "新增用户" : "编辑用户", ModalityType.APPLICATION_MODAL);
        this.userService = userService;
        this.user = user;
        setSize(450, 380);  // 高度增加以容纳密码行
        setLocationRelativeTo(owner);
        initUI();
        if (user != null) {
            loadData();
            // 管理员只读
            if (user.getRoleId() == 1) {
                nicknameField.setEditable(false);
                usernameField.setEditable(false);
                phoneField.setEditable(false);
                emailField.setEditable(false);
                sexCombo.setEnabled(false);
                roleCombo.setEnabled(false);
                passwordField.setEnabled(false);
                okBtn.setEnabled(false);
                setTitle("查看管理员信息");
            }
        }
        if (user == null) {
            roleCombo.setSelectedIndex(0);
            roleCombo.setEnabled(false);
        }
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
        addRow("角色:", roleCombo = new JComboBox<>(new String[]{"健康管家"}), gbc, row++);
        addRow("密码:", passwordField = new JPasswordField(15), gbc, row++);

        JPanel btnPanel = new JPanel();
        okBtn = new JButton("保存");
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
        sexCombo.setSelectedItem(user.getSex() == 1 ? "男" : "女");
        // 角色下拉框只有一个选项，无需设置索引
        if (user.getPassword() != null) {
            passwordField.setText(user.getPassword());
        }
    }

    private void save() {
        try {
            if (user == null) user = new User();
            user.setNickname(nicknameField.getText().trim());
            user.setUsername(usernameField.getText().trim());
            user.setPhoneNumber(phoneField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setSex(sexCombo.getSelectedIndex() == 0 ? 1 : 0);
            user.setRoleId(2);  // 固定为健康管家（因为下拉框只有这个选项）

            // 处理密码
            String newPassword = new String(passwordField.getPassword());
            if (!newPassword.isEmpty()) {
                user.setPassword(newPassword);
            } else if (user.getId() == null) {
                // 新增时若密码为空，使用默认规则（手机号后6位）
                if (user.getPhoneNumber() != null && user.getPhoneNumber().length() >= 6) {
                    user.setPassword(user.getPhoneNumber().substring(user.getPhoneNumber().length() - 6));
                } else {
                    user.setPassword("123456");
                }
            }
            // 编辑时如果密码框为空，保持原密码不变（不执行 setPassword，保持旧值）

            if (user.getId() == null) {
                User added = userService.addUser(user);
                if (added == null) {
                    UIUtils.showError(this, "用户名已存在");
                    return;
                }
            } else {
                // 编辑时再次检查是否是管理员（防止绕过界面）
                if (user.getRoleId() == 1) {
                    UIUtils.showError(this, "不能修改管理员信息");
                    return;
                }
                userService.updateUser(user);
            }
            success = true;
            dispose();
        } catch (Exception ex) {
            UIUtils.showError(this, "保存失败: " + ex.getMessage());
        }
    }

    /**
     * 判断保存是否成功
     *
     * @return 保存是否成功
     */

    public boolean isSuccess() { return success; }
}