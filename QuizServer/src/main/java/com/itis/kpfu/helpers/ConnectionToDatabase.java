package com.itis.kpfu.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionToDatabase {

    private static volatile Connection conn = null;

    public static synchronized Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        if (conn == null)
            synchronized (ConnectionToDatabase.class) {
                if (conn == null)
                    conn = DriverManager.getConnection(
                            "jdbc:postgresql://localhost:5432/quiz",
                            "postgres",
                            "1"
                    );
            }
        return conn;
    }


    private ConnectionToDatabase() {}
}
