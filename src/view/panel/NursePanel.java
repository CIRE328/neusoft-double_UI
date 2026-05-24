package view.panel;

import service.NurseService;
import view.dialog.*;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.*;

public class NursePanel extends JPanel {
    private NurseService nurseService = new NurseService();

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