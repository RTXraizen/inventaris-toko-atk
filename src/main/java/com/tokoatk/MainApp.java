package com.tokoatk;

import javafx.scene.image.Image; // <-- TAMBAHKAN IMPORT INI
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException; 

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            
            primaryStage.setTitle("Sistem Inventaris Toko ATK");
            primaryStage.setScene(scene);
            
            // --- KODE ICON ANDA DIMULAI DI SINI ---
            try {
                // Pastikan 'icon.png' ada di package com.tokoatk
                Image icon = new Image(getClass().getResourceAsStream("iconzoro.jpeg"));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                // Ini akan muncul di log jika icon.png tidak ditemukan
                System.err.println("Gagal memuat icon: " + e.getMessage());
            }
            // --- BATAS AKHIR KODE ICON ---
            
            primaryStage.setMaximized(true); 
            
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