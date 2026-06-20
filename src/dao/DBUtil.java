package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库工具类
 * 提供数据库连接的获取功能
 * 使用 MySQL 数据库，配置信息通过静态常量定义
 */

public class DBUtil {

    /**
     * 数据库连接 URL
     * 连接到本地 MySQL 服务器上的 neuH 数据库
     * 禁用 SSL，设置时区为 UTC
     */

    private static final String URL = "jdbc:mysql://localhost:3306/neu?useSSL=false&serverTimezone=UTC";

    /**
     * 数据库用户名和密码
     */

    private static final String USER = "root";
    private static final String PASSWORD = "203526";

    /**
     * 静态初始化块
     * 加载 MySQL JDBC 驱动
     * JDBC 4.0 后通常可以省略显式加载，但显式加载更稳妥
     */

    static {
        try {
            // 加载 MySQL 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     * 使用预配置的 URL、用户名和密码建立连接
     *
     * @return 数据库连接对象
     * @throws SQLException 如果连接数据库失败
     */

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}