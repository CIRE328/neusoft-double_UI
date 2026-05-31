package view.util;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import java.awt.*;

/**
 * UI 通用工具类
 * 提供全局字体设置、窗口居中、带标题面板创建及消息对话框等辅助方法
 */

public class UIUtils {

    /**
     * 设置全局字体
     *
     * @param font 要应用的字体
     */

    public static void setGlobalFont(Font font) {
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("TabbedPane.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("MenuItem.font", font);
    }

    /**
     * 创建带标题边框的面板
     *
     * @param title   边框标题
     * @param content 面板内容组件
     * @return 带标题边框的面板
     */

    public static JPanel createTitledPanel(String title, JComponent content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title,
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14)));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 将窗口居中显示于屏幕
     *
     * @param window 要居中的窗口
     */

    public static void centerWindow(Window window) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension wSize = window.getSize();
        window.setLocation((screen.width - wSize.width) / 2,
                (screen.height - wSize.height) / 2);
    }

    /**
     * 显示错误消息对话框
     *
     * @param parent  父组件
     * @param message 错误信息
     */

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "错误", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 显示提示信息对话框
     *
     * @param parent  父组件
     * @param message 提示信息
     */

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示确认对话框
     *
     * @param parent  父组件
     * @param message 确认信息
     * @return 用户是否选择"是"
     */

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "确认",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
