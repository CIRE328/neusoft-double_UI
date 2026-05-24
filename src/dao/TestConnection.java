package dao;

import java.sql.Connection;
import java.sql.DriverManager;


public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/neu?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "CjQ123456!";
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("连接成功！" + conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}