package view.panel;

import service.MealService;
import javax.swing.*;
import java.awt.*;

/**
 * 膳食管理面板
 * 以选项卡集成食品管理、客户饮食喜好及膳食日历三个子面板
 */

public class MealPanel extends JPanel {
    private MealService mealService = new MealService();

    /**
     * 构造函数
     * 初始化膳食管理选项卡布局
     */

    public MealPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("食品管理", new FoodPanel(mealService));
        tabPane.addTab("客户饮食喜好", new PreferencePanel(mealService));
        tabPane.addTab("膳食日历", new MealCalendarPanel(mealService));
        add(tabPane, BorderLayout.CENTER);
    }
}