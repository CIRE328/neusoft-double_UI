package view.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

/**
 * 表格 UI 工具类
 * 提供表格样式设置及列宽自动调整等辅助方法
 */

public class TableUtils {

    /**
     * 设置表格基本样式
     * 包括行高、字体、表头样式及内容居中对齐
     *
     * @param table 目标表格
     */

    public static void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setIntercellSpacing(new Dimension(10, 5));
        table.setShowVerticalLines(false);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 35));
        header.setReorderingAllowed(false);
        // 内容居中
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
    }

    /**
     * 根据单元格内容自动调整列宽
     *
     * @param table 目标表格
     */

    public static void autoResizeColumns(JTable table) {
        TableColumnModel colModel = table.getColumnModel();
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn col = colModel.getColumn(i);
            int maxWidth = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                Object val = table.getValueAt(row, i);
                String str = val == null ? "" : val.toString();
                FontMetrics fm = table.getFontMetrics(table.getFont());
                int width = fm.stringWidth(str) + 30;
                maxWidth = Math.max(maxWidth, width);
            }
            col.setPreferredWidth(Math.min(maxWidth, 300));
        }
    }
}