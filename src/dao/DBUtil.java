package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库工具类，用于获取数据库连接
 *
 * @author haowan996
 * @version 1.38
 */

public class DBUtil {

    /**
     * 数据库连接URL、用户名、密码
     */

    private static final String URL = "jdbc:mysql://localhost:3306/neu?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "CjQ123456!";

    /**
     * 静态代码块，加载MySQL驱动
     */

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接对象
     * @throws SQLException 当获取连接失败时抛出
     */

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}