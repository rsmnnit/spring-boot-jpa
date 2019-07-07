package com.practice.spring.connection;

import java.sql.Connection;
import java.sql.DriverManager;
//Unused class
public class DataSource {
    private static Connection connection;

    public DataSource() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JPADemo", "root", "root");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
