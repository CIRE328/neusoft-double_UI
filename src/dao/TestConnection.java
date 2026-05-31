package dao;

import java.sql.Connection;
import java.sql.DriverManager;


public class TestConnection {
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