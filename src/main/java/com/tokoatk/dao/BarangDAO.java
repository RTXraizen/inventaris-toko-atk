package com.tokoatk.dao;

import com.tokoatk.model.Barang;
import com.tokoatk.model.Kategori;
import com.tokoatk.model.Supplier;
import com.tokoatk.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BarangDAO {
    
    public ObservableList<Barang> getAllBarang() {
        ObservableList<Barang> listBarang = FXCollections.observableArrayList();
        // Query diperbarui TANPA kolom kontak_person
        String sql = "SELECT b.*, k.nama_kategori, s.nama_supplier, s.nomor_telepon, s.alamat " +
                     "FROM barang b " +
                     "LEFT JOIN kategori k ON b.kategori_id = k.kategori_id " +
                     "LEFT JOIN supplier s ON b.supplier_id = s.supplier_id " +
                     "ORDER BY b.nama_barang ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Kategori k = new Kategori(
                        rs.getInt("kategori_id"), 
                        rs.getString("nama_kategori")
                );
                
                // Membuat objek Supplier dengan constructor yang sudah diperbarui (tanpa kontak_person)
                Supplier s = new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("nama_supplier"),
                        rs.getString("nomor_telepon"),
                        rs.getString("alamat")
                );
                
                Barang b = new Barang(
                        rs.getInt("barang_id"),
                        rs.getString("nama_barang"),
                        rs.getString("deskripsi"),
                        rs.getInt("stok"),
                        rs.getDouble("harga_jual"),
                        k,
                        s
                );
                listBarang.add(b);
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil barang: " + e.getMessage());
            e.printStackTrace();
        }
        return listBarang;
    }

    public boolean addBarang(Barang barang) {
        String sql = "INSERT INTO barang (nama_barang, deskripsi, stok, harga_jual, kategori_id, supplier_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, barang.getNamaBarang());
            pstmt.setString(2, barang.getDeskripsi());
            pstmt.setInt(3, barang.getStok());
            pstmt.setDouble(4, barang.getHargaJual());
            pstmt.setInt(5, barang.getKategori().getKategoriId());

            if (barang.getSupplier() != null && barang.getSupplier().getSupplierId() > 0) {
                pstmt.setInt(6, barang.getSupplier().getSupplierId());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saat menambah barang: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBarang(Barang barang) {
        String sql = "UPDATE barang SET nama_barang = ?, deskripsi = ?, stok = ?, " +
                     "harga_jual = ?, kategori_id = ?, supplier_id = ? WHERE barang_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, barang.getNamaBarang());
            pstmt.setString(2, barang.getDeskripsi());
            pstmt.setInt(3, barang.getStok());
            pstmt.setDouble(4, barang.getHargaJual());
            pstmt.setInt(5, barang.getKategori().getKategoriId());

            if (barang.getSupplier() != null && barang.getSupplier().getSupplierId() > 0) {
                pstmt.setInt(6, barang.getSupplier().getSupplierId());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            pstmt.setInt(7, barang.getBarangId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saat update barang: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBarang(int barangId) {
        String sql = "DELETE FROM barang WHERE barang_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, barangId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saat menghapus barang: " + e.getMessage());
            return false;
        }
    }
}