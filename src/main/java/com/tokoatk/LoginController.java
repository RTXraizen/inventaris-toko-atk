package com.tokoatk;

import com.tokoatk.dao.PenggunaDAO;
import com.tokoatk.model.Pengguna;
import java.io.IOException;
import javafx.concurrent.Task; // <-- IMPORT BARU
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.image.Image;

/**
 * Controller (Otak) untuk file Login.fxml.
 * VERSI FINAL 2.0: Transisi mulus antar jendela.
 */
public class LoginController {

    @FXML
    private TextField fieldUsername;
    @FXML
    private PasswordField fieldPassword;
    @FXML
    private Button btnLogin;
    @FXML
    private Label labelError;

    private final PenggunaDAO penggunaDAO = new PenggunaDAO();

    /**
     * Metode ini akan dipanggil saat tombol "Login" di FXML diklik.
     */
    @FXML
    private void handleLoginButton(ActionEvent event) {
        String username = fieldUsername.getText();
        String password = fieldPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            tampilkanError("Username dan password tidak boleh kosong.");
            return;
        }

        Task<Pengguna> loginTask = new Task<Pengguna>() {
            @Override
            protected Pengguna call() throws Exception {
                // Ini berjalan di background thread
                return penggunaDAO.cekLogin(username, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            Pengguna pengguna = loginTask.getValue(); 
            
            if (pengguna != null) {
                // Login BERHASIL
                System.out.println("Login sukses! Selamat datang, " + pengguna.getNamaLengkap());
                
                // === INI PERBAIKANNYA ===
                // 1. Ambil stage (jendela) login saat ini
                Stage loginStage = (Stage) btnLogin.getScene().getWindow();
                
                // 2. Buka jendela inventaris BARU, dan kirim jendela login
                //    agar bisa ditutup dari sana.
                bukaJendelaInventaris(pengguna, loginStage);
                // Kita tidak lagi memanggil tutupJendelaLogin() dari sini.
                
            } else {
                // Login GAGAL
                tampilkanError("Username atau password salah.");
                kembalikanTombol();
            }
        });

        loginTask.setOnFailed(e -> {
            tampilkanError("Gagal terhubung ke database. Cek koneksi internet.");
            kembalikanTombol();
            loginTask.getException().printStackTrace(); 
        });

        // Ubah UI (Sekarang juga!)
        labelError.setVisible(false);
        btnLogin.setDisable(true);
        btnLogin.setText("Mencoba login...");

        new Thread(loginTask).start();
    }

    private void tampilkanError(String pesan) {
        labelError.setText(pesan);
        labelError.setVisible(true);
    }
    
    private void kembalikanTombol() {
        btnLogin.setDisable(false);
        btnLogin.setText("Login");
    }

    // HAPUS method tutupJendelaLogin() yang lama, kita tidak membutuhkannya lagi.

    /**
     * Helper method untuk membuka jendela inventaris (FXMLDocument.fxml).
     * PERUBAHAN: Sekarang menerima 'stageToClose' (jendela login)
     */
    private void bukaJendelaInventaris(Pengguna pengguna, Stage stageToClose) {
        try {
            // 1. Muat FXML (proses ini butuh waktu)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            Parent root = loader.load();

            // 2. Siapkan stage (jendela) BARU
            Stage inventarisStage = new Stage();
            inventarisStage.setTitle("Sistem Inventaris Toko ATK - Login sebagai: " + pengguna.getNamaLengkap());
            inventarisStage.setScene(new Scene(root));
            inventarisStage.setMaximized(true);
            
            try {
                Image icon = new Image(getClass().getResourceAsStream("iconzoro.jpeg"));
                inventarisStage.getIcons().add(icon);
            } catch (Exception e) {
                System.err.println("Gagal memuat icon jendela utama: " + e.getMessage());
            }
            
            // 3. Tampilkan jendela inventaris BARU
            inventarisStage.show();
            
            // 4. SETELAH jendela baru muncul, TUTUP jendela login LAMA
            stageToClose.close(); 
            
        } catch (IOException e) {
            System.err.println("Gagal memuat FXMLDocument.fxml: " + e.getMessage());
            e.printStackTrace();
            // Jika gagal, kembalikan tombol login agar pengguna bisa coba lagi
            tampilkanError("Gagal memuat halaman utama.");
            kembalikanTombol();
        }
    }
}