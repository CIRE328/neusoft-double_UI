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

public class UserPanel extends JPanel {
    private UserService userService = new UserService();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public UserPanel() {
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        // 查询栏
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        searchPanel.add(new JLabel("姓名/账号:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("查询");
        JButton refreshBtn = new JButton("刷新");
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);
        add(searchPanel, BorderLayout.NORTH);

        // 表格列
        String[] cols = {"ID", "姓名", "账号", "性别", "手机号", "邮箱", "角色", "操作"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 7;
            }
        };
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        new ButtonColumn(table, "编辑", 7, this::editUser);
        new ButtonColumn(table, "重置密码", 8, this::resetPassword);
        new ButtonColumn(table, "删除", 9, this::deleteUser);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 底部按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addBtn = new JButton("新增用户");
        addBtn.addActionListener(e -> addUser());
        btnPanel.add(addBtn);
        add(btnPanel, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> search());
        refreshBtn.addActionListener(e -> loadData());
    }

    private void loadData() { loadData(null); }
    private void search() {
        String kw = searchField.getText().trim();
        loadData(kw.isEmpty() ? null : kw);
    }

    private void loadData(String keyword) {
        tableModel.setRowCount(0);
        List<User> list;
        if (keyword == null) {
            list = userService.findAllUsers();
        } else {
            list = userService.findUsersByName(keyword);
            if (list.isEmpty()) {
                list = userService.findAllUsers().stream()
                        .filter(u -> u.getUsername().contains(keyword))
                        .toList();
            }
        }
        for (User u : list) {
            String sex = u.getSex() == 1 ? "男" : "女";
            String role = u.getRoleId() == 1 ? "管理员" : "健康管家";
            tableModel.addRow(new Object[]{
                    u.getId(), u.getNickname(), u.getUsername(), sex,
                    u.getPhoneNumber(), u.getEmail(), role, "编辑", "重置密码", "删除"
            });
        }
        TableUtils.autoResizeColumns(table);
    }

    private void addUser() {
        UserDialog dialog = new UserDialog(SwingUtilities.getWindowAncestor(this), userService, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) loadData();
    }

    private void editUser(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        User user = userService.findUserById(id).orElse(null);
        if (user != null) {
            UserDialog dialog = new UserDialog(SwingUtilities.getWindowAncestor(this), userService, user);
            dialog.setVisible(true);
            if (dialog.isSuccess()) loadData();
        }
    }

    private void resetPassword(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        if (UIUtils.confirm(this, "确定重置密码为手机号后6位吗？")) {
            boolean success = userService.resetPassword(id);
            if (success) {
                UIUtils.showInfo(this, "密码已重置");
                loadData();
            } else {
                UIUtils.showError(this, "重置失败");
            }
        }
    }

    private void deleteUser(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        if (UIUtils.confirm(this, "确定删除该用户吗？")) {
            boolean success = userService.deleteUser(id);
            if (success) {
                UIUtils.showInfo(this, "删除成功");
                loadData();
            } else {
                UIUtils.showError(this, "删除失败");
            }
        }
    }
}