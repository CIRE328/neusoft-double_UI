package view;

import pojo.User;
import view.panel.*;
import view.util.UIUtils;
import javax.swing.*;

public class AdminFrame extends JFrame {
    private User currentUser;

    public AdminFrame(User user) {
        this.currentUser = user;
        setTitle("东软颐养中心 - 管理员 [" + user.getNickname() + "]");
        setSize(1300, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        UIUtils.centerWindow(this);
        initTabs();
        setVisible(true);
    }

    private void initTabs() {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab("客户管理", new CustomerPanel());
        tabbedPane.addTab("床位管理", new BedPanel());
        tabbedPane.addTab("护理管理", new NursePanel());
        tabbedPane.addTab("健康管家管理", new HousekeeperPanel());
        tabbedPane.addTab("用户管理", new UserPanel());
        tabbedPane.addTab("膳食管理", new MealPanel());
        tabbedPane.addTab("统计信息", new StatisticsPanel());
        getContentPane().add(tabbedPane);
    }
}
