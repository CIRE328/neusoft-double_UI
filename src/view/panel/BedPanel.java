package view.panel;

import dao.RoomDao;
import pojo.Bed;
import pojo.BedDetails;
import pojo.Customer;
import service.BedService;
import service.CustomerService;
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

/**
 * 床位管理面板
 * 展示床位列表与统计信息，支持床位调换、示意图查看及使用详情查询
 */

public class BedPanel extends JPanel {
    private BedService bedService = new BedService();
    private RoomDao roomDao = new RoomDao();
    private CustomerService customerService = new CustomerService();
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statsLabel;

    /**
     * 构造函数
     * 初始化床位管理界面并加载数据
     */

    public BedPanel() {
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        statsLabel = new JLabel(" ", SwingConstants.CENTER);
        statsLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        statsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(statsLabel, BorderLayout.NORTH);

        String[] columns = {"床位ID", "房间号", "床位号", "状态", "操作"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4;
            }
        };
        table = new JTable(tableModel);
        TableUtils.styleTable(table);

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        new ButtonColumn(table, "调换", 4, this::onChangeBed);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("刷新");
        JButton schematicBtn = new JButton("床位示意图");
        JButton queryDetailBtn = new JButton("使用详情查询");
        bottomPanel.add(refreshBtn);
        bottomPanel.add(schematicBtn);
        bottomPanel.add(queryDetailBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadData());
        schematicBtn.addActionListener(e -> showBedSchematic());
        queryDetailBtn.addActionListener(e -> showBedUsageQueryDialog());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Bed> beds = bedService.getAllBeds();
        Map<String, Integer> stats = bedService.getBedStatistics();
        statsLabel.setText(String.format("总床位：%d     空闲：%d     有人：%d     外出：%d",
                stats.get("total"), stats.get("free"), stats.get("occupied"), stats.get("outward")));

        for (Bed bed : beds) {
            String status;
            if (bed.getBedStatus() == 1) status = "空闲";
            else if (bed.getBedStatus() == 2) status = "有人";
            else status = "外出";
            tableModel.addRow(new Object[]{
                    bed.getId(), bed.getRoomNo(), bed.getBedNo(), status, "调换"
            });
        }
        TableUtils.autoResizeColumns(table);
    }

    private void onChangeBed(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer bedId = (Integer) tableModel.getValueAt(row, 0);
        String status = (String) tableModel.getValueAt(row, 3);

        if (!"有人".equals(status)) {
            UIUtils.showError(this, "只能为当前有人入住的床位进行调换");
            return;
        }

        Integer customerId = bedService.getCustomerIdByBedId(bedId);
        if (customerId == null) {
            UIUtils.showError(this, "该床位没有绑定客户，无法调换");
            return;
        }

        BedChangeDialog dialog = new BedChangeDialog(SwingUtilities.getWindowAncestor(this), customerId, bedId);
        dialog.setVisible(true);
        loadData();
    }

    private void showBedSchematic() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "床位示意图", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // 获取所有楼层
        List<String> floors = bedService.getAllFloors();
        if (floors.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "暂无房间数据");
            dialog.dispose();
            return;
        }

        // 楼层选择下拉框
        JComboBox<String> floorCombo = new JComboBox<>(floors.toArray(new String[0]));
        floorCombo.setSelectedIndex(0); // 默认第一层
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("选择楼层："));
        topPanel.add(floorCombo);
        dialog.add(topPanel, BorderLayout.NORTH);

        // 用于显示示意图的面板
        JPanel schematicPanel = new JPanel();
        schematicPanel.setLayout(new BoxLayout(schematicPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(schematicPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // 加载指定楼层的示意图
        Runnable loadSchematic = () -> {
            schematicPanel.removeAll();
            String selectedFloor = (String) floorCombo.getSelectedItem();
            if (selectedFloor == null) return;

            List<Map<String, Object>> roomsWithBeds = bedService.getRoomsWithBedsByFloor(selectedFloor);
            for (Map<String, Object> roomInfo : roomsWithBeds) {
                Integer roomNo = (Integer) roomInfo.get("roomNo");
                List<Bed> beds = (List<Bed>) roomInfo.get("beds");

                JPanel roomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                roomPanel.setBorder(BorderFactory.createTitledBorder("房间 " + roomNo));
                for (Bed bed : beds) {
                    String statusText;
                    Color color;
                    switch (bed.getBedStatus()) {
                        case 1: statusText = "空闲"; color = new Color(144, 238, 144); break;
                        case 2: statusText = "有人"; color = new Color(255, 182, 193); break;
                        case 3: statusText = "外出"; color = new Color(255, 228, 181); break;
                        default: statusText = "未知"; color = Color.LIGHT_GRAY;
                    }
                    JLabel label = new JLabel(bed.getBedNo() + " (" + statusText + ")");
                    label.setOpaque(true);
                    label.setBackground(color);
                    label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    label.setPreferredSize(new Dimension(100, 30));
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    roomPanel.add(label);
                }
                schematicPanel.add(roomPanel);
                schematicPanel.add(Box.createVerticalStrut(10));
            }
            schematicPanel.revalidate();
            schematicPanel.repaint();
        };

        // 监听楼层变化
        floorCombo.addActionListener(e -> loadSchematic.run());
        // 初始加载
        loadSchematic.run();

        dialog.setVisible(true);
    }

    private void showBedUsageQueryDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "床位使用详情查询", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // 上半部分：两种查询方式
        JPanel queryPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        queryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 复合查询（原有功能，使用 queryBedDetails）
        JPanel compoundPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        compoundPanel.setBorder(BorderFactory.createTitledBorder("复合查询"));
        JTextField customerNameField = new JTextField();
        JTextField checkinDateField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"全部", "当前使用", "历史使用"});
        compoundPanel.add(new JLabel("客户姓名（模糊）："));
        compoundPanel.add(customerNameField);
        compoundPanel.add(new JLabel("入住日期（YYYY-MM-DD）："));
        compoundPanel.add(checkinDateField);
        compoundPanel.add(new JLabel("使用状态："));
        compoundPanel.add(statusCombo);
        JButton compoundBtn = new JButton("复合查询");

        // 客户ID查询（利用 getBedUsageDetails）
        JPanel idPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        idPanel.setBorder(BorderFactory.createTitledBorder("按客户ID查询床位历史"));
        JTextField customerIdField = new JTextField();
        JComboBox<String> detailStatusCombo = new JComboBox<>(new String[]{"当前使用", "历史使用", "全部"});
        JButton idBtn = new JButton("查询床位历史");
        idPanel.add(new JLabel("客户ID："));
        idPanel.add(customerIdField);
        idPanel.add(new JLabel("使用状态："));
        idPanel.add(detailStatusCombo);
        idPanel.add(idBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(compoundPanel, BorderLayout.NORTH);
        topPanel.add(idPanel, BorderLayout.SOUTH);
        queryPanel.add(topPanel);
        dialog.add(queryPanel, BorderLayout.NORTH);

        // 结果表格
        String[] cols = {"客户ID", "客户姓名", "床位ID", "房间号", "床位号", "开始日期", "结束日期", "使用状态"};
        DefaultTableModel resultModel = new DefaultTableModel(cols, 0);
        JTable resultTable = new JTable(resultModel);
        TableUtils.styleTable(resultTable);
        dialog.add(new JScrollPane(resultTable), BorderLayout.CENTER);

        // 复合查询事件
        compoundBtn.addActionListener(e -> {
            resultModel.setRowCount(0);
            String customerName = customerNameField.getText().trim();
            if (customerName.isEmpty()) customerName = null;
            java.util.Date checkinDate = null;
            try {
                if (!checkinDateField.getText().trim().isEmpty()) {
                    checkinDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(checkinDateField.getText().trim());
                }
            } catch (Exception ex) {
                UIUtils.showError(dialog, "日期格式错误，请使用 yyyy-MM-dd");
                return;
            }
            String usageStatus = (String) statusCombo.getSelectedItem();
            String statusParam = "全部".equals(usageStatus) ? null : usageStatus;

            List<BedDetails> details = bedService.queryBedDetails(customerName, checkinDate, statusParam);
            fillTable(resultModel, resultTable, details);
        });

        // 按客户ID查询事件（调用 getBedUsageDetails）
        idBtn.addActionListener(e -> {
            resultModel.setRowCount(0);
            String idStr = customerIdField.getText().trim();
            if (idStr.isEmpty()) {
                UIUtils.showError(dialog, "请输入客户ID");
                return;
            }
            Integer customerId;
            try {
                customerId = Integer.parseInt(idStr);
            } catch (NumberFormatException ex) {
                UIUtils.showError(dialog, "客户ID必须是数字");
                return;
            }
            String usage = (String) detailStatusCombo.getSelectedItem();
            List<BedDetails> details;
            if ("全部".equals(usage)) {
                // 分别获取后合并
                List<BedDetails> current = bedService.getBedUsageDetails(customerId, "当前使用");
                List<BedDetails> history = bedService.getBedUsageDetails(customerId, "历史使用");
                details = new java.util.ArrayList<>();
                details.addAll(current);
                details.addAll(history);
            } else {
                details = bedService.getBedUsageDetails(customerId, usage);
            }
            fillTable(resultModel, resultTable, details);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(compoundBtn);
        btnPanel.add(idBtn);
        queryPanel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void fillTable(DefaultTableModel model, JTable table, List<BedDetails> details) {
        model.setRowCount(0);
        for (BedDetails d : details) {
            String customerNameStr = customerService.findCustomerById(d.getCustomerId())
                    .map(Customer::getCustomerName).orElse("未知");
            Integer roomNo = bedService.getAllBeds().stream()
                    .filter(b -> b.getId().equals(d.getBedId()))
                    .findFirst().map(Bed::getRoomNo).orElse(0);
            String bedNo = bedService.getAllBeds().stream()
                    .filter(b -> b.getId().equals(d.getBedId()))
                    .findFirst().map(Bed::getBedNo).orElse("");
            String usage = d.getEndDate() == null ? "当前使用" : "历史使用";
            model.addRow(new Object[]{
                    d.getCustomerId(), customerNameStr, d.getBedId(), roomNo, bedNo,
                    d.getStartDate(), d.getEndDate(), usage
            });
        }
        TableUtils.autoResizeColumns(table);
    }
}