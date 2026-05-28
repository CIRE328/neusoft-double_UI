package view.panel;

import pojo.Customer;
import pojo.CustomerNurseItem;
import service.CustomerService;
import service.HousekeeperService;
import service.NurseService;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DailyNursingPanel extends JPanel {
    private HousekeeperService housekeeperService = new HousekeeperService();
    private NurseService nurseService = new NurseService();
    private CustomerService customerService = new CustomerService();
    private Integer housekeeperId;
    private JComboBox<String> customerCombo;
    private JTable itemTable;
    private DefaultTableModel itemModel;
    private List<Customer> myCustomers;
    private Integer selectedCustomerId;

    public DailyNursingPanel(Integer housekeeperId) {
        this.housekeeperId = housekeeperId;
        setLayout(new BorderLayout());
        initUI();
        loadCustomers();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("选择客户:"));
        customerCombo = new JComboBox<>();
        customerCombo.addActionListener(e -> customerChanged());
        topPanel.add(customerCombo);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"项目ID", "项目名称", "剩余次数", "到期日期", "操作"};
        itemModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 4; }
        };
        itemTable = new JTable(itemModel);
        new view.component.ButtonColumn(itemTable, "执行护理", 4, this::performNursing);
        add(new JScrollPane(itemTable), BorderLayout.CENTER);
    }

    private void loadCustomers() {
        myCustomers = housekeeperService.findCustomersByHousekeeper(housekeeperId);
        customerCombo.removeAllItems();
        for (Customer c : myCustomers) {
            customerCombo.addItem(c.getId() + " - " + c.getCustomerName());
        }
        if (!myCustomers.isEmpty()) {
            customerCombo.setSelectedIndex(0);
        }
    }

    private void customerChanged() {
        if (customerCombo.getSelectedIndex() < 0) return;
        String selected = (String) customerCombo.getSelectedItem();
        selectedCustomerId = Integer.parseInt(selected.split(" - ")[0]);
        loadItems();
    }

    private void loadItems() {
        itemModel.setRowCount(0);
        if (selectedCustomerId == null) return;
        List<CustomerNurseItem> items = nurseService.getCustomerNurseItems(selectedCustomerId);
        for (CustomerNurseItem cni : items) {
            String itemName = nurseService.findAllNurseContents().stream()
                    .filter(c -> c.getId().equals(cni.getItemId()))
                    .findFirst().map(c -> c.getNursingName()).orElse("未知");
            itemModel.addRow(new Object[]{
                    cni.getItemId(), itemName, cni.getNurseNumber(),
                    cni.getMaturityTime(), "执行"
            });
        }
    }

    private void performNursing(java.awt.event.ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer itemId = (Integer) itemModel.getValueAt(row, 0);
        String countStr = JOptionPane.showInputDialog(this, "护理次数:");
        if (countStr == null) return;
        int count = Integer.parseInt(countStr);
        boolean success = nurseService.performNursing(selectedCustomerId, itemId, count, housekeeperId);
        if (success) {
            UIUtils.showInfo(this, "护理记录已生成");
            loadItems();
        } else {
            UIUtils.showError(this, "护理失败，请检查剩余次数或有效期");
        }
    }
}
