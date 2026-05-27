package view;

import com.formdev.flatlaf.FlatLightLaf;
import service.UserService;
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
            userService.initAdmins();  // 自动创建默认管理员
            new LoginFrame().setVisible(true);
        });
    }
}