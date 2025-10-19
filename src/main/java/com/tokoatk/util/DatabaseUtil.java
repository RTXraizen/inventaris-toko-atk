package com.tokoatk.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String URL = "jdbc:postgresql://localhost:5432/db_inventaris_atk";
    private static final String USER = "postgres";
    private static final String PASSWORD = "ryan"; // Pastikan ini password Anda

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            // PERBAIKAN: Cek apakah koneksi null ATAU sudah ditutup
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Koneksi database baru berhasil dibuat!");
            }
        } catch (SQLException e) {
            System.err.println("Koneksi database GAGAL: " + e.getMessage());
            return null;
        }
        return connection;
    }
}