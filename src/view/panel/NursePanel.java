package view.panel;

import service.NurseService;
import view.dialog.*;
import javax.swing.*;
import java.awt.*;

/**
 * 护理管理面板
 * 以选项卡集成护理项目、护理级别及客户护理设置三个子面板
 */

public class NursePanel extends JPanel {
    private NurseService nurseService = new NurseService();

    /**
     * 构造函数
     * 初始化护理管理选项卡布局
     */

    public NursePanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("护理项目", createItemPanel());
        tabPane.addTab("护理级别", createLevelPanel());
        tabPane.addTab("客户护理设置", createCustomerNursePanel());
        add(tabPane, BorderLayout.CENTER);
    }

    private JPanel createItemPanel() {
        return new NurseItemPanel(nurseService);
    }

    private JPanel createLevelPanel() {
        return new NurseLevelPanel(nurseService);
    }

    private JPanel createCustomerNursePanel() {
        return new CustomerNurseSettingPanel(nurseService);
    }
}