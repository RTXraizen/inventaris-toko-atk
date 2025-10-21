package com.tokoatk.dao;

import com.tokoatk.model.Supplier;
import com.tokoatk.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SupplierDAO {

    // --- METODE BARU ---
    // Untuk mengosongkan supplier_id di tabel barang sebelum supplier dihapus
    public void unlinkBarangFromSupplier(int supplierId) {
        String sql = "UPDATE barang SET supplier_id = NULL WHERE supplier_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, supplierId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error saat melepaskan kaitan barang dari supplier: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean isSupplierInUse(int supplierId) {
        String sql = "SELECT COUNT(*) FROM barang WHERE supplier_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, supplierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengecek penggunaan supplier: " + e.getMessage());
            return true; 
        }
        return false;
    }

    public ObservableList<Supplier> getAllSuppliers() {
        ObservableList<Supplier> listSupplier = FXCollections.observableArrayList();
        String sql = "SELECT * FROM supplier ORDER BY nama_supplier ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Supplier s = new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("nama_supplier"),
                        rs.getString("nomor_telepon"),
                        rs.getString("alamat")
                );
                listSupplier.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil data supplier: " + e.getMessage());
        }
        return listSupplier;
    }

    public boolean addSupplier(Supplier supplier) {
        String sql = "INSERT INTO supplier (nama_supplier, nomor_telepon, alamat) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplier.getNamaSupplier());
            pstmt.setString(2, supplier.getNomorTelepon());
            pstmt.setString(3, supplier.getAlamat());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saat menambah supplier: " + e.getMessage());
            return false;
        }
    }

    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE supplier SET nama_supplier = ?, nomor_telepon = ?, alamat = ? WHERE supplier_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplier.getNamaSupplier());
            pstmt.setString(2, supplier.getNomorTelepon());
            pstmt.setString(3, supplier.getAlamat());
            pstmt.setInt(4, supplier.getSupplierId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saat update supplier: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSupplier(int supplierId) {
        String sql = "DELETE FROM supplier WHERE supplier_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saat menghapus supplier: " + e.getMessage());
            e.printStackTrace(); 
            return false;
        }
    }
}