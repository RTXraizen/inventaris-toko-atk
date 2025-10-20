package com.tokoatk;

import javafx.scene.image.Image; // Import ini masih dipakai
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException; 

/**
 * Kelas utama yang menjalankan seluruh aplikasi.
 * File ini sekarang dimodifikasi untuk memuat Login.fxml terlebih dahulu.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // === INI BAGIAN YANG DIUBAH ===
            // 1. Muat file Login.fxml, bukan FXMLDocument.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            
            // 2. Beri judul untuk jendela login
            primaryStage.setTitle("Login - Sistem Inventaris Toko ATK");
            primaryStage.setScene(scene);
            
            // 3. (Opsional) Beri icon untuk jendela login
            try {
                // Pastikan 'iconzoro.jpeg' ada di package com.tokoatk
                Image icon = new Image(getClass().getResourceAsStream("iconzoro.jpeg"));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                System.err.println("Gagal memuat icon: " + e.getMessage());
            }
            
            // 4. Jangan di-maximize, biarkan ukuran login window apa adanya
            // primaryStage.setMaximized(true); // <-- Baris ini dinonaktifkan
            
            primaryStage.show();
            
        } catch (IOException e) {
            System.err.println("Gagal memuat FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}