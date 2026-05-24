package view.panel;

import pojo.Bed;
import service.BedService;
import view.component.ButtonColumn;
import view.dialog.BedChangeDialog;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

public class BedPanel extends JPanel {
    private BedService bedService = new BedService();
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statsLabel;

    public BedPanel() {
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        // 顶部统计栏
        statsLabel = new JLabel(" ", SwingConstants.CENTER);
        statsLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        statsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(statsLabel, BorderLayout.NORTH);

        // 表格列：房间号、床位号、状态、操作
        String[] columns = {"床位ID", "房间号", "床位号", "状态", "操作"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 3; // 操作列可编辑
            }
        };
        table = new JTable(tableModel);
        TableUtils.styleTable(table);

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        // 添加操作按钮列
        new ButtonColumn(table, "调换", 3, this::onChangeBed);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 底部刷新按钮
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> loadData());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Bed> beds = bedService.getAllBeds(); // 需在 BedService 中添加
        Map<String, Integer> stats = bedService.getBedStatistics();
        statsLabel.setText(String.format("总床位：%d     空闲：%d     有人：%d     外出：%d",
                stats.get("total"), stats.get("free"), stats.get("occupied"), stats.get("outward")));

        for (Bed bed : beds) {
            String status;
            if (bed.getBedStatus() == 1) status = "空闲";
            else if (bed.getBedStatus() == 2) status = "有人";
            else status = "外出";
            tableModel.addRow(new Object[]{bed.getId(), bed.getRoomNo(), bed.getBedNo(), status, "调换"});
        }
        TableUtils.autoResizeColumns(table);
    }

    private void onChangeBed(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        // 获取选中行的床位ID（需要隐藏列或从原始数据中获取）
        // 简单方案：通过房间号和床位号去查询，但最好在表格中增加隐藏列存储床位ID
        // 我们修改表格模型，增加隐藏列 ID，但当前显示列没有ID。为简化，重新设计数据模型
        // 实际开发中建议在表格中加一个隐藏列存储床位ID。这里为了演示，我们获取行数据后去查询
        Integer bedId = (Integer) tableModel.getValueAt(row, 0);
        Integer roomNo = (Integer) tableModel.getValueAt(row, 0);
        String bedNo = (String) tableModel.getValueAt(row, 1);
        // 根据房间号和床位号获取床位对象
        Bed bed = bedService.getAllBeds().stream()
                .filter(b -> b.getRoomNo().equals(roomNo) && b.getBedNo().equals(bedNo))
                .findFirst().orElse(null);
        if (bed == null) return;
        if (bed.getBedStatus() != 2) {
            UIUtils.showError(this, "只能为当前有人入住的床位进行调换");
            return;
        }
        // 打开调换对话框，传入当前床位ID和客户ID（需要获取客户ID）
        // 注意：需要知道该床位对应的客户ID。可以从床位使用详情中查询
        Integer customerId = bedService.getCustomerIdByBedId(bed.getId()); // 需要新增方法
        if (customerId == null) {
            UIUtils.showError(this, "该床位没有绑定客户");
            return;
        }
        BedChangeDialog dialog = new BedChangeDialog(SwingUtilities.getWindowAncestor(this), customerId, bed.getId());
        dialog.setVisible(true);
        loadData(); // 刷新
    }
}