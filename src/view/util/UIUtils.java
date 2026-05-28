package view.util;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import java.awt.*;

public class UIUtils {

    // 设置全局字体
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

    // 创建带标题边框的面板
    public static JPanel createTitledPanel(String title, JComponent content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title,
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14)));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    // 窗口居中
    public static void centerWindow(Window window) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension wSize = window.getSize();
        window.setLocation((screen.width - wSize.width) / 2,
                (screen.height - wSize.height) / 2);
    }

    // 显示错误消息
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "错误", JOptionPane.ERROR_MESSAGE);
    }

    // 显示信息消息
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    // 确认对话框
    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "确认",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
