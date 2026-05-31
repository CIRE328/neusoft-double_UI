package view.panel;

import service.BedService;
import service.CustomerService;
import service.NurseService;
import service.StatisticsService;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Map;

public class StatisticsPanel extends JPanel {
    private StatisticsService statisticsService = new StatisticsService();
    private BedService bedService = new BedService();
    private CustomerService customerService = new CustomerService();
    private NurseService nurseService = new NurseService();

    private JLabel bedStatsLabel, customerStatsLabel, recordStatsLabel;
    private JTextArea customerDetailArea, nurseRecordArea;

    public StatisticsPanel() {
        setLayout(new BorderLayout());
        initUI();
        loadStatistics();
    }

    private void initUI() {
        JTabbedPane tabPane = new JTabbedPane();

        // 统计概览面板
        JPanel overviewPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        overviewPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        bedStatsLabel = new JLabel();
        bedStatsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        customerStatsLabel = new JLabel();
        customerStatsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        recordStatsLabel = new JLabel();
        recordStatsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        overviewPanel.add(bedStatsLabel);
        overviewPanel.add(customerStatsLabel);
        overviewPanel.add(recordStatsLabel);
        tabPane.addTab("统计概览", overviewPanel);

        // 客户详细信息面板（自理老人/护理老人列表）
        JPanel customerDetailPanel = new JPanel(new BorderLayout());
        customerDetailArea = new JTextArea();
        customerDetailArea.setEditable(false);
        customerDetailArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        customerDetailPanel.add(new JScrollPane(customerDetailArea), BorderLayout.CENTER);
        tabPane.addTab("客户详情", customerDetailPanel);

        // 护理记录详情面板（最近10条）
        JPanel nurseRecordPanel = new JPanel(new BorderLayout());
        nurseRecordArea = new JTextArea();
        nurseRecordArea.setEditable(false);
        nurseRecordArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        nurseRecordPanel.add(new JScrollPane(nurseRecordArea), BorderLayout.CENTER);
        tabPane.addTab("最近护理记录", nurseRecordPanel);

        // 刷新按钮
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> loadStatistics());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(refreshBtn);
        add(tabPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadStatistics() {
        // 床位统计
        Map<String, Integer> bedStats = statisticsService.getBedStatistics();
        bedStatsLabel.setText(String.format("【床位统计】总床位：%d   空闲：%d   有人：%d   外出：%d",
                bedStats.get("total"), bedStats.get("free"),
                bedStats.get("occupied"), bedStats.get("outward")));

        // 客户统计
        Map<String, Integer> customerStats = statisticsService.getCustomerStatistics();
        customerStatsLabel.setText(String.format("【客户统计】总数：%d   自理老人：%d   护理老人：%d",
                customerStats.get("total"), customerStats.get("selfCare"), customerStats.get("nursingCare")));

        // 护理记录统计
        long recordCount = statisticsService.getNurseRecordCount(null);
        recordStatsLabel.setText(String.format("【护理记录】总记录数：%d", recordCount));

        // 客户详情
        StringBuilder customerDetail = new StringBuilder();
        var allCustomers = customerService.findAllCustomers();
        long selfCare = allCustomers.stream().filter(c -> c.getLevelId() == null).count();
        long nursingCare = allCustomers.size() - selfCare;
        customerDetail.append("自理老人列表（无护理级别）：\n");
        allCustomers.stream().filter(c -> c.getLevelId() == null)
                .forEach(c -> customerDetail.append(c.getId()).append(" ").append(c.getCustomerName()).append("\n"));
        customerDetail.append("\n护理老人列表（有护理级别）：\n");
        allCustomers.stream().filter(c -> c.getLevelId() != null)
                .forEach(c -> customerDetail.append(c.getId()).append(" ").append(c.getCustomerName())
                        .append(" 级别ID:").append(c.getLevelId()).append("\n"));
        customerDetailArea.setText(customerDetail.toString());

        // 最近10条护理记录
        StringBuilder recordDetail = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        var records = nurseService.getNurseRecordsByCustomer(null); // 获取所有记录，按时间倒序
        records.stream()
                .sorted((r1, r2) -> r2.getNursingTime().compareTo(r1.getNursingTime()))
                .limit(10)
                .forEach(r -> {
                    String customerName = customerService.findCustomerById(r.getCustomerId())
                            .map(c -> c.getCustomerName()).orElse("未知");
                    String itemName = nurseService.findAllNurseContents().stream()
                            .filter(i -> i.getId().equals(r.getItemId()))
                            .findFirst().map(i -> i.getNursingName()).orElse("未知");
                    recordDetail.append(sdf.format(r.getNursingTime()))
                            .append(" 客户：").append(customerName)
                            .append(" 项目：").append(itemName)
                            .append(" 次数：").append(r.getNursingCount())
                            .append("\n");
                });
        if (recordDetail.length() == 0) recordDetail.append("暂无护理记录");
        nurseRecordArea.setText(recordDetail.toString());
    }
}