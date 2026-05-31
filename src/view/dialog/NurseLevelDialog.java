package view.dialog;

import pojo.NurseLevel;
import service.NurseService;
import javax.swing.*;
import java.awt.*;

/**
 * 护理级别编辑对话框
 * 用于新增或编辑护理级别的名称与状态
 */

public class NurseLevelDialog extends JDialog {
    private NurseService nurseService;
    private NurseLevel level;
    private boolean success = false;
    private JTextField nameField;
    private JComboBox<String> statusCombo;

    /**
     * 构造函数
     *
     * @param owner        父窗口
     * @param nurseService 护理服务
     * @param level        待编辑的护理级别，为 null 时表示新增
     */

    public NurseLevelDialog(Window owner, NurseService nurseService, NurseLevel level) {
        super(owner, level == null ? "新增护理级别" : "编辑护理级别", ModalityType.APPLICATION_MODAL);
        this.nurseService = nurseService;
        this.level = level;
        setSize(300, 150);
        setLocationRelativeTo(owner);
        initUI();
        if (level != null) loadData();
    }

    private void initUI() {
        setLayout(new GridLayout(3, 2, 10, 10));
        add(new JLabel("级别名称:"));
        nameField = new JTextField();
        add(nameField);
        add(new JLabel("状态:"));
        statusCombo = new JComboBox<>(new String[]{"启用", "停用"});
        add(statusCombo);
        JButton okBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        add(okBtn);
        add(cancelBtn);
        okBtn.addActionListener(e -> save());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void loadData() {
        nameField.setText(level.getLevelName());
        statusCombo.setSelectedIndex(level.getLevelStatus() == 1 ? 0 : 1);
    }

    private void save() {
        if (level == null) level = new NurseLevel();
        level.setLevelName(nameField.getText().trim());
        level.setLevelStatus(statusCombo.getSelectedIndex() == 0 ? 1 : 2);
        if (level.getId() == null) {
            nurseService.addNurseLevel(level);
        } else {
            nurseService.updateNurseLevel(level);
        }
        success = true;
        dispose();
    }

    /**
     * 判断保存是否成功
     *
     * @return 保存是否成功
     */

    public boolean isSuccess() { return success; }
}
