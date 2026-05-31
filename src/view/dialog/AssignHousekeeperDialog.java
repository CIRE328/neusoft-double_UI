package view.dialog;

import pojo.Customer;
import pojo.User;
import service.HousekeeperService;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 分配管家对话框
 * 为无管家的客户选择健康管家并完成分配
 */

public class AssignHousekeeperDialog extends JDialog {
    private HousekeeperService housekeeperService;
    private boolean success = false;
    private JComboBox<String> customerCombo;
    private JComboBox<String> housekeeperCombo;
    private List<Customer> customersWithoutHousekeeper;
    private List<User> housekeepers;

    /**
     * 构造函数
     * 加载无管家客户与管家列表并显示对话框
     *
     * @param owner              父窗口
     * @param housekeeperService 健康管家服务
     */

    public AssignHousekeeperDialog(Window owner, HousekeeperService housekeeperService) {
        super(owner, "分配管家", ModalityType.APPLICATION_MODAL);
        this.housekeeperService = housekeeperService;
        setSize(400, 200);
        setLocationRelativeTo(owner);
        initUI();
        loadData();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("选择客户:"), gbc);
        gbc.gridx = 1;
        customerCombo = new JComboBox<>();
        add(customerCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("选择管家:"), gbc);
        gbc.gridx = 1;
        housekeeperCombo = new JComboBox<>();
        add(housekeeperCombo, gbc);

        JButton okBtn = new JButton("确定");
        JButton cancelBtn = new JButton("取消");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, gbc);

        okBtn.addActionListener(e -> assign());
        cancelBtn.addActionListener(e -> dispose());
        pack();
    }

    private void loadData() {
        // 使用 findCustomersWithoutHousekeeper 获取无管家客户
        customersWithoutHousekeeper = housekeeperService.findCustomersWithoutHousekeeper();
        if (customersWithoutHousekeeper.isEmpty()) {
            customerCombo.addItem("暂无无管家客户");
            customerCombo.setEnabled(false);
        } else {
            for (Customer c : customersWithoutHousekeeper) {
                customerCombo.addItem(c.getId() + " - " + c.getCustomerName());
            }
        }

        housekeepers = housekeeperService.findAllHousekeepers();
        if (housekeepers.isEmpty()) {
            housekeeperCombo.addItem("暂无健康管家");
            housekeeperCombo.setEnabled(false);
        } else {
            for (User u : housekeepers) {
                housekeeperCombo.addItem(u.getId() + " - " + u.getNickname());
            }
        }
    }

    private void assign() {
        if (customerCombo.getItemCount() == 0 || housekeeperCombo.getItemCount() == 0) {
            UIUtils.showError(this, "没有可分配的数据");
            return;
        }
        String selectedCustomer = (String) customerCombo.getSelectedItem();
        String selectedHousekeeper = (String) housekeeperCombo.getSelectedItem();
        if (selectedCustomer == null || selectedHousekeeper == null) return;
        if (selectedCustomer.contains("暂无") || selectedHousekeeper.contains("暂无")) return;

        Integer customerId = Integer.parseInt(selectedCustomer.split(" - ")[0]);
        Integer housekeeperId = Integer.parseInt(selectedHousekeeper.split(" - ")[0]);
        boolean ok = housekeeperService.assignHousekeeper(customerId, housekeeperId);
        if (ok) {
            success = true;
            dispose();
        } else {
            UIUtils.showError(this, "分配失败");
        }
    }

    /**
     * 判断分配是否成功
     *
     * @return 分配是否成功
     */

    public boolean isSuccess() { return success; }
}