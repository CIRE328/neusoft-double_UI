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
 * 表格中的按钮列，用于放置编辑、删除等操作按钮
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
        actionListener.actionPerformed(new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "" + row));
        fireEditingStopped();
    }

    @Override
    public Object getCellEditorValue() {
        return text;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());
        if (col == column && row >= 0 && table.isEditing()) {
            table.getCellEditor(row, col).getTableCellEditorComponent(table, null, true, row, col);
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}