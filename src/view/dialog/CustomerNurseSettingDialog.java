package view.dialog;

import pojo.Customer;
import pojo.CustomerNurseItem;
import pojo.NurseContent;
import pojo.NurseLevel;
import service.CustomerService;
import service.NurseService;
import view.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerNurseSettingDialog extends JDialog {
    private NurseService nurseService;
    private CustomerService customerService = new CustomerService();
    private Integer customerId;
    private Customer customer;
    private JTable itemTable;
    private DefaultTableModel itemModel;

    public CustomerNurseSettingDialog(Window owner, NurseService nurseService, Integer customerId) {
        super(owner, "客户护理设置", ModalityType.APPLICATION_MODAL);
        this.nurseService = nurseService;
        this.customerId = customerId;
        this.customer = customerService.findCustomerById(customerId).orElse(null);
        if (customer == null) {
            UIUtils.showError(owner, "客户不存在");
            dispose();
            return;
        }
        setSize(700, 500);
        setLocationRelativeTo(owner);
        initUI();
        loadItems();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        // 顶部客户信息
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("客户：" + customer.getCustomerName()));
        infoPanel.add(new JLabel("当前护理级别：" + (customer.getLevelId() == null ? "无" : customer.getLevelId())));
        add(infoPanel, BorderLayout.NORTH);

        // 中间表格：已购买护理项目
        String[] cols = {"ID", "项目名称", "剩余次数", "到期日期", "续费","移除"};
        itemModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 4; }
        };
        itemTable = new JTable(itemModel);
        new view.component.ButtonColumn(itemTable, "续费", 4, this::renewItem);
        new view.component.ButtonColumn(itemTable, "移除", 5, this::removeItem);
        add(new JScrollPane(itemTable), BorderLayout.CENTER);

        // 底部按钮
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton setLevelBtn = new JButton("设置护理级别");
        JButton removeLevelBtn = new JButton("移除护理级别");
        JButton purchaseBtn = new JButton("购买护理项目");
        btnPanel.add(setLevelBtn);
        btnPanel.add(removeLevelBtn);
        btnPanel.add(purchaseBtn);
        add(btnPanel, BorderLayout.SOUTH);

        setLevelBtn.addActionListener(e -> setLevel());
        removeLevelBtn.addActionListener(e -> removeLevel());
        purchaseBtn.addActionListener(e -> purchaseItem());
    }

    private void loadItems() {
        itemModel.setRowCount(0);
        List<CustomerNurseItem> items = nurseService.getCustomerNurseItems(customerId);
        for (CustomerNurseItem cni : items) {
            NurseContent content = nurseService.findAllNurseContents().stream()
                    .filter(c -> c.getId().equals(cni.getItemId())).findFirst().orElse(null);
            String name = content == null ? "未知" : content.getNursingName();
            itemModel.addRow(new Object[]{
                    cni.getId(), name, cni.getNurseNumber(),
                    cni.getMaturityTime(), "续费", "移除"
            });
        }
    }

    private void setLevel() {
        List<NurseLevel> levels = nurseService.findAllNurseLevels();
        if (levels.isEmpty()) {
            UIUtils.showError(this, "没有可用的护理级别");
            return;
        }
        String[] levelNames = levels.stream().map(l -> l.getId() + " - " + l.getLevelName()).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this, "选择护理级别", "设置级别",
                JOptionPane.QUESTION_MESSAGE, null, levelNames, levelNames[0]);
        if (selected != null) {
            Integer levelId = Integer.parseInt(selected.split(" - ")[0]);
            boolean success = nurseService.setCustomerLevel(customerId, levelId);
            if (success) {
                customer.setLevelId(levelId);
                UIUtils.showInfo(this, "设置成功");
                loadItems();
                dispose(); // 关闭对话框，让主面板刷新
            } else {
                UIUtils.showError(this, "设置失败，可能已有级别");
            }
        }
    }

    private void removeLevel() {
        if (customer.getLevelId() == null) {
            UIUtils.showError(this, "该客户没有护理级别");
            return;
        }
        if (UIUtils.confirm(this, "移除级别会删除当前级别下的所有护理项目，确定吗？")) {
            boolean success = nurseService.removeCustomerLevel(customerId);
            if (success) {
                customer.setLevelId(null);
                UIUtils.showInfo(this, "移除成功");
                loadItems();
            } else {
                UIUtils.showError(this, "移除失败");
            }
        }
    }

    private void purchaseItem() {
        // 显示可购买的护理项目（启用且未购买的）
        List<NurseContent> all = nurseService.findAllNurseContents().stream()
                .filter(c -> c.getStatus() == 1).collect(Collectors.toList());
        List<Integer> ownedIds = nurseService.getCustomerNurseItems(customerId).stream()
                .map(CustomerNurseItem::getItemId).collect(Collectors.toList());
        List<NurseContent> available = all.stream().filter(c -> !ownedIds.contains(c.getId())).collect(Collectors.toList());
        if (available.isEmpty()) {
            UIUtils.showInfo(this, "没有可购买的护理项目");
            return;
        }
        String[] itemNames = available.stream().map(c -> c.getId() + " - " + c.getNursingName() + " (" + c.getServicePrice() + "元)").toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this, "选择护理项目", "购买",
                JOptionPane.QUESTION_MESSAGE, null, itemNames, itemNames[0]);
        if (selected != null) {
            Integer itemId = Integer.parseInt(selected.split(" - ")[0]);
            String quantityStr = JOptionPane.showInputDialog(this, "购买数量:");
            if (quantityStr == null) return;
            int quantity = Integer.parseInt(quantityStr);
            String dateStr = JOptionPane.showInputDialog(this, "到期日期 (yyyy-MM-dd):");
            Date maturity = null;
            try {
                maturity = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            } catch (Exception ex) {
                UIUtils.showError(this, "日期格式错误");
                return;
            }
            boolean success = nurseService.purchaseNurseItem(customerId, itemId, quantity, maturity);
            if (success) {
                UIUtils.showInfo(this, "购买成功");
                loadItems();
            } else {
                UIUtils.showError(this, "购买失败");
            }
        }
    }

    private void renewItem(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer cniId = (Integer) itemModel.getValueAt(row, 0);
        String addStr = JOptionPane.showInputDialog(this, "增加次数:");
        if (addStr == null) return;
        int add = Integer.parseInt(addStr);
        String newDateStr = JOptionPane.showInputDialog(this, "新到期日期 (yyyy-MM-dd，直接回车不变):");
        Date newMaturity = null;
        if (newDateStr != null && !newDateStr.isEmpty()) {
            try {
                newMaturity = new SimpleDateFormat("yyyy-MM-dd").parse(newDateStr);
            } catch (Exception ex) {}
        }
        boolean success = nurseService.renewNurseItem(cniId, add, newMaturity);
        if (success) {
            UIUtils.showInfo(this, "续费成功");
            loadItems();
        } else {
            UIUtils.showError(this, "续费失败");
        }
    }

    private void removeItem(ActionEvent e) {
        int row = Integer.parseInt(e.getActionCommand());
        Integer cniId = (Integer) itemModel.getValueAt(row, 0);
        if (UIUtils.confirm(this, "确定移除该护理项目吗？")) {
            boolean success = nurseService.removeCustomerNurseItem(cniId);
            if (success) {
                UIUtils.showInfo(this, "移除成功");
                loadItems();
            } else {
                UIUtils.showError(this, "移除失败");
            }
        }
    }
}
