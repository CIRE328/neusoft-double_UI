package dao;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 数据库连接测试类
 * 用于验证 MySQL 数据源配置是否正确
 */

public class TestConnection {

    /**
     * 程序入口，尝试建立数据库连接并输出结果
     *
     * @param args 命令行参数（未使用）
     */

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/neuH?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "Hly207724";
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("连接成功！" + conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
