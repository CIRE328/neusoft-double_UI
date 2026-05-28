package view.dialog;

import pojo.Bed;
import pojo.Customer;
import service.BedService;
import service.CustomerService;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class RegisterDialog extends JDialog {
    private BedService bedService;
    private CustomerService customerService = new CustomerService();
    private boolean success = false;

    private JTextField nameField, idcardField, phoneField, familyField, birthdayField, checkinField, expireField;
    private JComboBox<String> roomCombo, bedCombo, sexCombo, bloodCombo;

    public RegisterDialog(Window owner, BedService bedService) {
        super(owner, "入住登记", ModalityType.APPLICATION_MODAL);
        this.bedService = bedService;
        setSize(500, 620);
        setLocationRelativeTo(owner);
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        int row = 0;

        addRow("姓名：", nameField = new JTextField(15), gbc, row++);
        addRow("性别：", sexCombo = new JComboBox<>(new String[]{"男", "女"}), gbc, row++);
        addRow("身份证号：", idcardField = new JTextField(18), gbc, row++);
        addRow("出生日期(YYYY-MM-DD)：", birthdayField = new JTextField(10), gbc, row++);
        addRow("血型：", bloodCombo = new JComboBox<>(new String[]{"A", "B", "AB", "O"}), gbc, row++);
        addRow("联系电话：", phoneField = new JTextField(15), gbc, row++);
        addRow("家属姓名：", familyField = new JTextField(15), gbc, row++);

        // 房间下拉（获取所有房间号）
        List<Integer> roomNumbers = bedService.getAllRoomNumbers(); // 需要在 BedService 中添加
        roomCombo = new JComboBox<>(roomNumbers.stream().map(String::valueOf).toArray(String[]::new));
        addRow("房间号：", roomCombo, gbc, row++);

        bedCombo = new JComboBox<>();
        addRow("床位号：", bedCombo, gbc, row++);
        roomCombo.addActionListener(e -> updateBeds());

        addRow("入住时间(YYYY-MM-DD)：", checkinField = new JTextField(10), gbc, row++);
        addRow("合同到期时间(YYYY-MM-DD)：", expireField = new JTextField(10), gbc, row++);

        JButton okBtn = new JButton("确定");
        JButton cancelBtn = new JButton("取消");
        JPanel btnPanel = new JPanel();
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        add(btnPanel, gbc);

        okBtn.addActionListener(e -> register());
        cancelBtn.addActionListener(e -> dispose());

        updateBeds();
        pack();
    }

    private void addRow(String label, Component comp, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        add(comp, gbc);
    }

    private void updateBeds() {
        String selectedRoom = (String) roomCombo.getSelectedItem();
        if (selectedRoom == null) return;
        int roomNo = Integer.parseInt(selectedRoom);
        List<Bed> freeBeds = bedService.getFreeBedsByRoom(roomNo);
        bedCombo.removeAllItems();
        for (Bed b : freeBeds) {
            bedCombo.addItem(b.getBedNo() + " (ID:" + b.getId() + ")");
        }
        if (freeBeds.isEmpty()) {
            bedCombo.addItem("无空闲床位");
        }
    }

    private void register() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Customer c = new Customer();
            c.setCustomerName(nameField.getText().trim());
            c.setCustomerSex(sexCombo.getSelectedIndex()); // 0男1女
            c.setIdcard(idcardField.getText().trim());
            c.setBirthday(sdf.parse(birthdayField.getText().trim()));
            c.setBloodType((String) bloodCombo.getSelectedItem());
            c.setContactTel(phoneField.getText().trim());
            c.setFamilyMember(familyField.getText().trim());
            c.setCheckinDate(sdf.parse(checkinField.getText().trim()));
            c.setExpirationDate(sdf.parse(expireField.getText().trim()));

            String bedItem = (String) bedCombo.getSelectedItem();
            if (bedItem == null || bedItem.contains("无空闲床位")) {
                UIUtils.showError(this, "请选择空闲床位");
                return;
            }
            int bedId = Integer.parseInt(bedItem.substring(bedItem.indexOf("ID:") + 3, bedItem.length() - 1));
            boolean ok = customerService.checkin(c, bedId);
            if (ok) {
                success = true;
                dispose();
                UIUtils.showInfo(this, "入住登记成功");
            } else {
                UIUtils.showError(this, "入住登记失败，请检查信息或床位状态");
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "输入格式错误：" + ex.getMessage());
        }
    }

    public boolean isSuccess() { return success; }
}
