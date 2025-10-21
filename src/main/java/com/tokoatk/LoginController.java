package com.tokoatk;

import com.tokoatk.dao.PenggunaDAO;
import com.tokoatk.model.Pengguna;
import java.io.IOException;
import javafx.concurrent.Task; 
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
import javafx.fxml.Initializable; 
import java.net.URL; 
import java.util.ResourceBundle; 
import javafx.scene.input.KeyCode; 

/**
 * Controller (Otak) untuk file Login.fxml.
 */
public class LoginController implements Initializable { 

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
     * == SETUP: Mengaktifkan Tombol Enter ==
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Saat fokus berada pada PasswordField dan tombol ditekan:
        fieldPassword.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLoginButton(new ActionEvent());
                event.consume(); 
            }
        });
        
        // Pindahkan fokus ke password jika Enter di Username field
        fieldUsername.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                fieldPassword.requestFocus(); 
                event.consume();
            }
        });
    }

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
                return penggunaDAO.cekLogin(username, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            Pengguna pengguna = loginTask.getValue(); 
            
            if (pengguna != null) {
                System.out.println("Login sukses! Selamat datang, " + pengguna.getNamaLengkap());
                
                Stage loginStage = (Stage) btnLogin.getScene().getWindow();
                bukaJendelaInventaris(pengguna, loginStage);
                
            } else {
                tampilkanError("Username atau password salah.");
                kembalikanTombol();
            }
        });

        loginTask.setOnFailed(e -> {
            tampilkanError("Gagal terhubung ke database. Cek koneksi internet.");
            kembalikanTombol();
            loginTask.getException().printStackTrace(); 
        });

        // Ubah UI
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

    /**
     * Helper method untuk membuka jendela inventaris (FXMLDocument.fxml).
     */
    private void bukaJendelaInventaris(Pengguna pengguna, Stage stageToClose) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            Parent root = loader.load();

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
            
            inventarisStage.show();
            stageToClose.close(); 
            
        } catch (IOException e) {
            System.err.println("Gagal memuat FXMLDocument.fxml: " + e.getMessage());
            e.printStackTrace();
            tampilkanError("Gagal memuat halaman utama.");
            kembalikanTombol();
        }
    }
}