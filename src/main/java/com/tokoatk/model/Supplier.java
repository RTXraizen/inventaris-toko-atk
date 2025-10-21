package com.tokoatk.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Supplier {
    private final IntegerProperty supplierId;
    private final StringProperty namaSupplier;
    private final StringProperty nomorTelepon;
    private final StringProperty alamat;

    // Konstruktor disederhanakan tanpa 'kontak'
    public Supplier(int id, String nama, String telepon, String alamat) {
        this.supplierId = new SimpleIntegerProperty(id);
        this.namaSupplier = new SimpleStringProperty(nama);
        this.nomorTelepon = new SimpleStringProperty(telepon);
        this.alamat = new SimpleStringProperty(alamat);
    }

    // --- Getters, Setters, dan Property Getters ---

    public int getSupplierId() { return supplierId.get(); }
    public IntegerProperty supplierIdProperty() { return supplierId; }

    public String getNamaSupplier() { return namaSupplier.get(); }
    public StringProperty namaSupplierProperty() { return namaSupplier; }
    public void setNamaSupplier(String nama) { this.namaSupplier.set(nama); }

    public String getNomorTelepon() { return nomorTelepon.get(); }
    public StringProperty nomorTeleponProperty() { return nomorTelepon; }
    public void setNomorTelepon(String telepon) { this.nomorTelepon.set(telepon); }

    public String getAlamat() { return alamat.get(); }
    public StringProperty alamatProperty() { return alamat; }
    public void setAlamat(String alamat) { this.alamat.set(alamat); }

    // Override toString agar nama tampil di ComboBox
    @Override
    public String toString() {
        return getNamaSupplier();
    }

    public void setTelepon(String telepon) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public String getTelepon() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}