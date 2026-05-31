package view;

import com.formdev.flatlaf.FlatLightLaf;
import service.UserService;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;

/**
 * 应用程序启动入口
 * 初始化 FlatLaf 外观、全局字体，创建默认管理员并显示登录窗口
 */

public class Launcher {

    /**
     * 程序主入口
     *
     * @param args 命令行参数
     */

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