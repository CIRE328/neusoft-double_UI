package view.panel;

import pojo.User;
import service.UserService;
import view.component.ButtonColumn;
import view.dialog.UserDialog;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理面板
 * 分上下两区展示管理员（只读）与健康管家（可增删改、重置密码）列表
 */

public class UserPanel extends JPanel {
    private UserService userService = new UserService();
    private JTable adminTable, nurseTable;
    private DefaultTableModel adminModel, nurseModel;
    private JTextField adminSearchField, nurseSearchField;

    /**
     * 构造函数
     * 初始化用户管理界面并加载数据
     */

    public UserPanel() {
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.4);

        // ==================== 管理员区域（只读） ====================
        JPanel adminPanel = new JPanel(new BorderLayout());
        adminPanel.setBorder(BorderFactory.createTitledBorder("管理员"));
        JPanel adminSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        adminSearchPanel.add(new JLabel("姓名/账号:"));
        adminSearchField = new JTextField(12);
        adminSearchPanel.add(adminSearchField);
        JButton adminSearchBtn = new JButton("查询");
        JButton adminRefreshBtn = new JButton("刷新");
        adminSearchPanel.add(adminSearchBtn);
        adminSearchPanel.add(adminRefreshBtn);
        adminPanel.add(adminSearchPanel, BorderLayout.NORTH);

        // 增加密码列
        String[] adminCols = {"ID", "姓名", "账号", "性别", "手机号", "邮箱", "密码"};
        adminModel = new DefaultTableModel(adminCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        adminTable = new JTable(adminModel);
        TableUtils.styleTable(adminTable);
        adminPanel.add(new JScrollPane(adminTable), BorderLayout.CENTER);
        splitPane.setTopComponent(adminPanel);

        // ==================== 健康管家区域（可操作） ====================
        JPanel nursePanel = new JPanel(new BorderLayout());
        nursePanel.setBorder(BorderFactory.createTitledBorder("健康管家"));
        JPanel nurseSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nurseSearchPanel.add(new JLabel("姓名/账号:"));
        nurseSearchField = new JTextField(12);
        nurseSearchPanel.add(nurseSearchField);
        JButton nurseSearchBtn = new JButton("查询");
        JButton nurseRefreshBtn = new JButton("刷新");
        nurseSearchPanel.add(nurseSearchBtn);
        nurseSearchPanel.add(nurseRefreshBtn);
        nursePanel.add(nurseSearchPanel, BorderLayout.NORTH);

        // 增加密码列（位于邮箱之后，操作列之前）
        String[] nurseCols = {"ID", "姓名", "账号", "性别", "手机号", "邮箱", "密码", "编辑", "重置密码", "删除"};
        nurseModel = new DefaultTableModel(nurseCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 7 || col == 8 || col == 9; // 操作列索引后移
            }
        };
        nurseTable = new JTable(nurseModel);
        TableUtils.styleTable(nurseTable);
        new ButtonColumn(nurseTable, "编辑", 7, this::editUser);
        new ButtonColumn(nurseTable, "重置密码", 8, this::resetPassword);
        new ButtonColumn(nurseTable, "删除", 9, this::deleteUser);
        nursePanel.add(new JScrollPane(nurseTable), BorderLayout.CENTER);

        JButton addBtn = new JButton("新增健康管家");
        addBtn.addActionListener(e -> addUser());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(addBtn);
        nursePanel.add(btnPanel, BorderLayout.SOUTH);

        splitPane.setBottomComponent(nursePanel);
        add(splitPane, BorderLayout.CENTER);

        adminSearchBtn.addActionListener(e -> searchAdmin());
        adminRefreshBtn.addActionListener(e -> refreshAdmin());
        nurseSearchBtn.addActionListener(e -> searchNurse());
        nurseRefreshBtn.addActionListener(e -> refreshNurse());
    }

    private void loadData() {
        loadAdminData(null);
        loadNurseData(null);
    }

    private void loadAdminData(String keyword) {
        adminModel.setRowCount(0);
        List<User> admins;
        if (keyword != null && !keyword.isEmpty()) {
            admins = userService.findUsersByName(keyword).stream()
                    .filter(u -> u.getRoleId() == 1)
                    .collect(Collectors.toList());
        } else {
            admins = userService.findUsersByRole(1);
        }
        for (User u : admins) {
            String sex = u.getSex() == 1 ? "男" : "女";
            adminModel.addRow(new Object[]{
                    u.getId(), u.getNickname(), u.getUsername(), sex,
                    u.getPhoneNumber(), u.getEmail(), "******"
            });
        }
        TableUtils.autoResizeColumns(adminTable);
    }

    private void loadNurseData(String keyword) {
        nurseModel.setRowCount(0);
        List<User> nurses;
        if (keyword != null && !keyword.isEmpty()) {
            nurses = userService.findUsersByName(keyword).stream()
                    .filter(u -> u.getRoleId() == 2)
                    .collect(Collectors.toList());
        } else {
            nurses = userService.findUsersByRole(2);
        }
        for (User u : nurses) {
            String sex = u.getSex() == 1 ? "男" : "女";
            String pwd = u.getPassword() == null ? "" : u.getPassword();
            nurseModel.addRow(new Object[]{
                    u.getId(), u.getNickname(), u.getUsername(), sex,
                    u.getPhoneNumber(), u.getEmail(), pwd, "编辑", "重置密码", "删除"
            });
        }
        TableUtils.autoResizeColumns(nurseTable);
    }

    private void searchAdmin() {
        String kw = adminSearchField.getText().trim();
        loadAdminData(kw.isEmpty() ? null : kw);
    }

    private void refreshAdmin() {
        adminSearchField.setText("");
        loadAdminData(null);
    }

    private void searchNurse() {
        String kw = nurseSearchField.getText().trim();
        loadNurseData(kw.isEmpty() ? null : kw);
    }

    private void refreshNurse() {
        nurseSearchField.setText("");
        loadNurseData(null);
    }

    private void addUser() {
        UserDialog dialog = new UserDialog(SwingUtilities.getWindowAncestor(this), userService, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            refreshNurse();
        }
    }

    private void editUser(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer id = (Integer) nurseModel.getValueAt(row, 0);
        User user = userService.findUserById(id).orElse(null);
        if (user != null) {
            UserDialog dialog = new UserDialog(SwingUtilities.getWindowAncestor(this), userService, user);
            dialog.setVisible(true);
            if (dialog.isSuccess()) refreshNurse();
        }
    }

    private void resetPassword(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer id = (Integer) nurseModel.getValueAt(row, 0);
        if (UIUtils.confirm(this, "确定重置密码为手机号后6位吗？")) {
            boolean success = userService.resetPassword(id);
            if (success) {
                UIUtils.showInfo(this, "密码已重置");
                refreshNurse();
            } else {
                UIUtils.showError(this, "重置失败");
            }
        }
    }

    private void deleteUser(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer id = (Integer) nurseModel.getValueAt(row, 0);
        if (UIUtils.confirm(this, "确定删除该健康管家吗？")) {
            boolean success = userService.deleteUser(id);
            if (success) {
                UIUtils.showInfo(this, "删除成功");
                refreshNurse();
            } else {
                UIUtils.showError(this, "删除失败");
            }
        }
    }
}