package view.component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StatusCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value != null) {
            String status = value.toString();
            if (status.equals("空闲") || status.equals("启用") || status.equals("通过") || status.equals("正常")) {
                c.setForeground(new Color(0, 128, 0)); // 绿色
            } else if (status.equals("有人") || status.equals("停用") || status.equals("拒绝") || status.equals("已删除")) {
                c.setForeground(Color.RED);
            } else if (status.equals("外出") || status.equals("待审核") || status.equals("已提交")) {
                c.setForeground(new Color(255, 140, 0)); // 橙色
            } else {
                c.setForeground(Color.BLACK);
            }
        }
        return c;
    }
}