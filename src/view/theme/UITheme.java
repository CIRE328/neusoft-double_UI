package view.theme;

import javax.swing.*;
import java.awt.*;

public class UITheme {
    public static void apply() {
        // 全局字体
        Font defaultFont = new Font("微软雅黑", Font.PLAIN, 14);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Table.font", new Font("微软雅黑", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("TextArea.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("TabbedPane.font", defaultFont);
        UIManager.put("Dialog.font", defaultFont);

        // 表格行高
        UIManager.put("Table.rowHeight", 28);
        // 选项卡高度
        UIManager.put("TabbedPane.tabHeight", 32);
    }
}