package com.tokoatk.dao;

import com.tokoatk.model.Pengguna;
import com.tokoatk.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO (Data Access Object) untuk semua logika terkait tabel 'pengguna'.
 * Fungsi utamanya adalah untuk memverifikasi login.
 */
public class PenggunaDAO {
    
    /**
     * Mengecek username dan password ke database.
     * @param username Username yang diinput pengguna.
     * @param password Password yang diinput pengguna.
     * @return Objek Pengguna jika login berhasil, atau null jika gagal.
     */
    public Pengguna cekLogin(String username, String password) {
        // Query SQL untuk mencari pengguna berdasarkan username DAN password
        String sql = "SELECT * FROM pengguna WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set parameter query (mencegah SQL Injection)
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                // Cek apakah data ditemukan
                if (rs.next()) {
                    // Jika login berhasil, buat objek Pengguna
                    System.out.println("Login berhasil untuk: " + username);
                    return new Pengguna(
                        rs.getInt("pengguna_id"),
                        rs.getString("username"),
                        rs.getString("nama_lengkap")
                    );
                } else {
                    // Jika username/password salah, tidak ada data yang ditemukan
                    System.err.println("Login gagal: Username atau password salah.");
                    return null;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error saat proses login: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}