package com.tokoatk.dao;

import com.tokoatk.model.Kategori;
import com.tokoatk.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class KategoriDAO {
    
    public ObservableList<Kategori> getAllKategori() {
        ObservableList<Kategori> listKategori = FXCollections.observableArrayList();
        String sql = "SELECT * FROM kategori ORDER BY nama_kategori ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                listKategori.add(new Kategori(
                        rs.getInt("kategori_id"), 
                        rs.getString("nama_kategori")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil kategori: " + e.getMessage());
        }
        return listKategori;
    }
}