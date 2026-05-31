package view.component;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 表格按钮列组件
 * 在 JTable 指定列中渲染可点击按钮，用于编辑、删除等行级操作
 */

public class ButtonColumn extends AbstractCellEditor
        implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener {

    private JTable table;
    private JButton renderButton;
    private JButton editButton;
    private String text;
    private int column;
    private int row;
    private ActionListener actionListener;

    /**
     * 构造函数
     * 在指定表格列注册按钮渲染器、编辑器及鼠标监听
     *
     * @param table          目标表格
     * @param text           按钮显示文字
     * @param column         列索引
     * @param actionListener 按钮点击回调，ActionEvent 的 actionCommand 为行号
     */

    public ButtonColumn(JTable table, String text, int column, ActionListener actionListener) {
        this.table = table;
        this.text = text;
        this.column = column;
        this.actionListener = actionListener;
        renderButton = new JButton(text);
        editButton = new JButton(text);
        editButton.setFocusPainted(false);
        editButton.addActionListener(this);
        table.getColumnModel().getColumn(column).setCellRenderer(this);
        table.getColumnModel().getColumn(column).setCellEditor(this);
        table.addMouseListener(this);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        renderButton.setText(text);
        renderButton.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return renderButton;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.row = row;
        this.column = column;
        editButton.setText(text);
        return editButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 先触发业务回调
        actionListener.actionPerformed(new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "" + row));

        // 检查当前行是否仍然在有效范围内（防止回调中修改了模型）
        if (row >= 0 && row < table.getModel().getRowCount()) {
            fireEditingStopped();
        } else {
            // 行已无效，直接取消编辑，避免触发异常
            fireEditingCanceled();
        }
    }

    @Override
    public Object getCellEditorValue() {
        return text;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());
        if (col == column && row >= 0) {
            // 如果单元格正在编辑，则启动编辑器；否则忽略，因为按钮已经通过 ActionListener 处理
            if (table.isEditing()) {
                table.getCellEditor(row, col).getTableCellEditorComponent(table, null, true, row, col);
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}