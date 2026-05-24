package view.panel;

import service.MealService;
import javax.swing.*;
import java.awt.*;

public class MealPanel extends JPanel {
    private MealService mealService = new MealService();

    public MealPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("食品管理", new FoodPanel(mealService));
        tabPane.addTab("客户饮食喜好", new PreferencePanel(mealService));
        tabPane.addTab("膳食日历", new MealCalendarPanel(mealService));
        add(tabPane, BorderLayout.CENTER);
    }
}