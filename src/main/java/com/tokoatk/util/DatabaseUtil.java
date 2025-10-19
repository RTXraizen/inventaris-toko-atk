package com.tokoatk.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {

    // Kita tidak lagi hardcode (menulis langsung) kredensial di sini
    private static Connection connection = null;
    private static Properties properties = new Properties();

    // Blok 'static' ini akan berjalan otomatis SATU KALI
    // saat aplikasi pertama kali mencoba menggunakan kelas ini.
    static {
        try (InputStream input = DatabaseUtil.class
                .getResourceAsStream("/com/tokoatk/db.properties")) {
            
            if (input == null) {
                // Ini adalah error fatal jika file db.properties tidak ada
                System.err.println("FATAL ERROR: File 'db.properties' tidak ditemukan!");
                System.err.println("Pastikan file itu ada di dalam package com.tokoatk");
                throw new RuntimeException("Gagal memuat db.properties: File tidak ditemukan.");
            }
            
            // Muat semua properti (url, user, password) dari file
            properties.load(input);
            System.out.println("File db.properties berhasil dimuat.");
            
        } catch (Exception e) {
            // Error jika file tidak bisa dibaca
            System.err.println("Gagal memuat db.properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Metode utama untuk mendapatkan koneksi ke database.
     * Sekarang akan membaca dari file properties.
     */
    public static Connection getConnection() {
        try {
            // Cek apakah koneksi perlu dibuat
            if (connection == null || connection.isClosed()) {
                
                // 1. Ambil kredensial dari file properties yang sudah dimuat
                String url = properties.getProperty("db.url");
                String user = properties.getProperty("db.user");
                String password = properties.getProperty("db.password");
                
                // 2. Cek apakah semua properti ada
                if (url == null || user == null || password == null) {
                    throw new SQLException("Kredensial database di db.properties tidak lengkap (url, user, atau password kosong).");
                }

                // 3. Buat koneksi baru menggunakan kredensial dari Supabase
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Koneksi database CLOUD (Supabase) berhasil dibuat!");
            }
        } catch (SQLException e) {
            System.err.println("Koneksi database CLOUD GAGAL: " + e.getMessage());
            e.printStackTrace(); // Tampilkan error lengkap di log
            return null;
        }
        // Kembalikan koneksi yang sudah ada atau yang baru dibuat
        return connection;
    }
}