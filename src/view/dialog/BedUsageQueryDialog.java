package view.dialog;

import pojo.Bed;
import pojo.BedDetails;
import pojo.Customer;
import service.BedService;
import service.CustomerService;
import view.util.TableUtils;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BedUsageQueryDialog extends JDialog {
    private BedService bedService;
    private CustomerService customerService = new CustomerService();
    private JTextField customerNameField;
    private JTextField checkinDateField;
    private JComboBox<String> statusCombo;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    public BedUsageQueryDialog(Window owner, BedService bedService) {
        super(owner, "床位使用详情查询", ModalityType.APPLICATION_MODAL);
        this.bedService = bedService;
        setSize(800, 500);
        setLocationRelativeTo(owner);
        initUI();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 查询条件面板
        JPanel queryPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        queryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        queryPanel.add(new JLabel("客户姓名（模糊）："));
        customerNameField = new JTextField();
        queryPanel.add(customerNameField);
        queryPanel.add(new JLabel("入住日期（YYYY-MM-DD）："));
        checkinDateField = new JTextField();
        queryPanel.add(checkinDateField);
        queryPanel.add(new JLabel("使用状态："));
        statusCombo = new JComboBox<>(new String[]{"全部", "当前使用", "历史使用"});
        queryPanel.add(statusCombo);

        JButton queryBtn = new JButton("查询");
        queryBtn.addActionListener(e -> query());
        queryPanel.add(queryBtn);

        add(queryPanel, BorderLayout.NORTH);

        // 结果表格
        String[] cols = {"客户ID", "客户姓名", "床位ID", "房间号", "床位号", "开始日期", "结束日期", "使用状态"};
        tableModel = new DefaultTableModel(cols, 0);
        resultTable = new JTable(tableModel);
        TableUtils.styleTable(resultTable);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);
    }

    private void query() {
        String customerName = customerNameField.getText().trim();
        if (customerName.isEmpty()) customerName = null;
        Date checkinDate = null;
        try {
            if (!checkinDateField.getText().trim().isEmpty()) {
                checkinDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkinDateField.getText().trim());
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "日期格式错误");
            return;
        }
        String usageStatus = (String) statusCombo.getSelectedItem();
        // 转换为 BedService.queryBedDetails 接受的参数格式（当前使用/历史使用/全部）
        String statusParam = "";
        if ("当前使用".equals(usageStatus)) statusParam = "当前使用";
        else if ("历史使用".equals(usageStatus)) statusParam = "历史使用";
        else statusParam = null;

        List<BedDetails> details = bedService.queryBedDetails(customerName, checkinDate, statusParam);
        tableModel.setRowCount(0);
        for (BedDetails d : details) {
            String customerNameStr = customerService.findCustomerById(d.getCustomerId())
                    .map(Customer::getCustomerName).orElse("未知");
            // 获取床位房间号（需通过 BedDao 查询）
            Integer roomNo = bedService.getAllBeds().stream()
                    .filter(b -> b.getId().equals(d.getBedId()))
                    .findFirst().map(Bed::getRoomNo).orElse(0);
            String bedNo = bedService.getAllBeds().stream()
                    .filter(b -> b.getId().equals(d.getBedId()))
                    .findFirst().map(Bed::getBedNo).orElse("");
            String usage = d.getEndDate() == null ? "当前使用" : "历史使用";
            tableModel.addRow(new Object[]{
                    d.getCustomerId(), customerNameStr, d.getBedId(), roomNo, bedNo,
                    d.getStartDate(), d.getEndDate(), usage
            });
        }
        TableUtils.autoResizeColumns(resultTable);
    }
}