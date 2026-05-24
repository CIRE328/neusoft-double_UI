package view;

import pojo.User;
import service.AuthService;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private AuthService authService = new AuthService();
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("东软颐养中心 - 登录");
        setSize(450, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        UIUtils.centerWindow(this);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标题
        JLabel titleLabel = new JLabel("东软颐养中心管理系统", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // 用户名
        gbc.gridy = 1; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("用户名："), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("密码："), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // 按钮
        JButton loginBtn = new JButton("登录");
        loginBtn.setPreferredSize(new Dimension(100, 30));
        JButton exitBtn = new JButton("退出");
        exitBtn.setPreferredSize(new Dimension(100, 30));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.add(loginBtn);
        btnPanel.add(exitBtn);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        mainPanel.add(btnPanel, gbc);

        add(mainPanel);

        loginBtn.addActionListener(e -> login());
        exitBtn.addActionListener(e -> System.exit(0));
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        User user = authService.login(username, password);
        if (user == null) {
            UIUtils.showError(this, "用户名或密码错误");
        } else {
            dispose();
            if (user.getRoleId() == 1) {
                new AdminFrame(user).setVisible(true);
            } else if (user.getRoleId() == 2) {
                new HousekeeperFrame(user).setVisible(true);
            } else {
                UIUtils.showError(null, "未知角色");
            }
        }
    }
}