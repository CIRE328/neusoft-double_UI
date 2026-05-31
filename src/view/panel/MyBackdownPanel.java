package view.panel;

import pojo.BackDown;
import pojo.Customer;
import service.CustomerService;
import view.util.TableUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 退住申请列表面板
 * 展示所有退住申请记录，支持按审批状态筛选
 */

public class MyBackdownPanel extends JPanel {
    private CustomerService customerService = new CustomerService();
    private Integer housekeeperId;  // 保留构造参数，但不用于过滤
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusCombo;

    /**
     * 构造函数
     *
     * @param housekeeperId 当前健康管家 ID（保留参数，列表展示全部申请）
     */

    public MyBackdownPanel(Integer housekeeperId) {
        this.housekeeperId = housekeeperId;
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("审批状态:"));
        statusCombo = new JComboBox<>(new String[]{"全部", "待审核", "已通过", "已拒绝"});
        JButton searchBtn = new JButton("查询");
        filterPanel.add(statusCombo);
        filterPanel.add(searchBtn);
        add(filterPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "客户姓名", "退住类型", "退住原因", "退住时间", "审批状态"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        TableUtils.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> loadData());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        // 获取所有退住申请，不再根据管家服务客户过滤
        List<BackDown> backdowns = customerService.findAllBackdowns();

        String statusFilter = (String) statusCombo.getSelectedItem();
        for (BackDown b : backdowns) {
            String status;
            if (b.getAuditstatus() == 0) status = "待审核";
            else if (b.getAuditstatus() == 1) status = "已通过";
            else status = "已拒绝";
            if (!"全部".equals(statusFilter) && !status.equals(statusFilter)) continue;

            // 获取客户姓名，如果客户已被删除则显示“已删除客户”
            String customerName = customerService.findCustomerById(b.getCustomerId())
                    .map(Customer::getCustomerName).orElse("已删除客户");
            String type = b.getRetreattype() == 0 ? "正常退住" : (b.getRetreattype() == 1 ? "死亡退住" : "保留床位");
            tableModel.addRow(new Object[]{
                    b.getId(), customerName, type, b.getRetreatmentreason(),
                    b.getRetreatment(), status
            });
        }
        TableUtils.autoResizeColumns(table);
    }
}