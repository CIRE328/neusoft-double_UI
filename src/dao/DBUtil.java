package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // 根据你的配置修改 URL、用户名、密码
    private static final String URL = "jdbc:mysql://localhost:3306/neu?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Hly207724";

    static {
        try {
            // 加载 MySQL 驱动 (JDBC 4.0 后通常可以省略，但显式加载更稳妥)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}