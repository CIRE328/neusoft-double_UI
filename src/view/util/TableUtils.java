package view.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class TableUtils {

    // 设置表格基本样式
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

    // 自动调整列宽（简单实现）
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