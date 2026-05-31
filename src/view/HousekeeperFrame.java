package view;

import pojo.User;
import view.panel.*;
import view.util.UIUtils;
import javax.swing.*;
import java.awt.BorderLayout;

/**
 * 健康管家主窗口
 * 提供我的客户、日常护理、护理记录、外出/退住申请及申请列表等功能选项卡
 */

public class HousekeeperFrame extends JFrame {
    private User currentUser;

    /**
     * 构造函数
     *
     * @param user 当前登录的健康管家用户
     */

    public HousekeeperFrame(User user) {
        this.currentUser = user;
        setTitle("东软颐养中心 - 健康管家 [" + user.getNickname() + "]");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        UIUtils.centerWindow(this);
        initTabs();
        setVisible(true);
    }

    private void initTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("我的客户", new MyCustomersPanel(currentUser.getId()));
        tabbedPane.addTab("日常护理", new DailyNursingPanel(currentUser.getId()));
        tabbedPane.addTab("护理记录", new NurseRecordPanel(currentUser.getId()));
        tabbedPane.addTab("外出申请", new ApplyOutwardPanel(currentUser.getId()));
        tabbedPane.addTab("退住申请", new ApplyBackdownPanel(currentUser.getId()));

        // 新增：我的申请（含外出和退住申请列表，以及回院登记）
        JPanel myApplicationPanel = new JPanel(new BorderLayout());
        JTabbedPane subTab = new JTabbedPane();
        subTab.addTab("外出申请列表", new MyOutwardPanel(currentUser.getId()));
        subTab.addTab("退住申请列表", new MyBackdownPanel(currentUser.getId()));
        myApplicationPanel.add(subTab, BorderLayout.CENTER);
        tabbedPane.addTab("我的申请", myApplicationPanel);

        getContentPane().add(tabbedPane);
    }
}