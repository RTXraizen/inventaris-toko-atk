package com.tokoatk.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {

    private static final Properties properties = new Properties();
    private static final String dbUrl;
    private static final String dbUser;
    private static final String dbPassword;

    // Blok ini hanya akan berjalan satu kali saat aplikasi dimulai
    static {
        try (InputStream input = DatabaseUtil.class.getResourceAsStream("/com/tokoatk/db.properties")) {
            
            if (input == null) {
                throw new RuntimeException("FATAL ERROR: File 'db.properties' tidak ditemukan!");
            }
            
            properties.load(input);
            dbUrl = properties.getProperty("db.url");
            dbUser = properties.getProperty("db.user");
            dbPassword = properties.getProperty("db.password");
            
            if (dbUrl == null || dbUser == null || dbPassword == null) {
                throw new RuntimeException("Kredensial database di db.properties tidak lengkap.");
            }
            System.out.println("Konfigurasi database berhasil dimuat.");
            
        } catch (Exception e) {
            // Jika gagal memuat properties, aplikasi tidak bisa lanjut.
            throw new RuntimeException("Gagal memuat db.properties: " + e.getMessage(), e);
        }
    }

    /**
     * Metode ini sekarang berfungsi sebagai 'Connection Factory'.
     * Setiap kali dipanggil, ia akan membuat dan mengembalikan koneksi BARU.
     * Pengelolaan (membuka & menutup) koneksi diserahkan sepenuhnya
     * ke pemanggil (yaitu blok try-with-resources di dalam DAO).
     */
    public static Connection getConnection() throws SQLException {
        // Selalu buat koneksi baru setiap kali diminta.
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}