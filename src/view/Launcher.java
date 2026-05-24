package view;

import com.formdev.flatlaf.FlatLightLaf;
import service.UserService;
import view.dialog.AdminRegisterDialog;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;

public class Launcher {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIUtils.setGlobalFont(new Font("微软雅黑", Font.PLAIN, 14));
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            UserService userService = new UserService();
            boolean hasAdmin = userService.findAllUsers().stream().anyMatch(u -> u.getRoleId() == 1);
            if (!hasAdmin) {
                // 弹出管理员注册对话框
                JFrame dummy = new JFrame();
                dummy.setUndecorated(true);
                dummy.setVisible(true);
                AdminRegisterDialog dialog = new AdminRegisterDialog(dummy);
                dummy.dispose();
                if (!dialog.isSuccess()) {
                    System.exit(0);
                }
            }
            new LoginFrame().setVisible(true);
        });
    }
}