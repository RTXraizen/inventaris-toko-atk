package com.tokoatk.model;

import javafx.beans.property.*;

public class Barang {
    private final IntegerProperty barangId;
    private final StringProperty namaBarang;
    private final StringProperty deskripsi;
    private final IntegerProperty stok;
    private final DoubleProperty hargaJual;
    private final ObjectProperty<Kategori> kategori;
    private final ObjectProperty<Supplier> supplier;

    public Barang(int id, String nama, String desk, int stok, double harga, Kategori kat, Supplier sup) {
        this.barangId = new SimpleIntegerProperty(id);
        this.namaBarang = new SimpleStringProperty(nama);
        this.deskripsi = new SimpleStringProperty(desk);
        this.stok = new SimpleIntegerProperty(stok);
        this.hargaJual = new SimpleDoubleProperty(harga);
        this.kategori = new SimpleObjectProperty<>(kat);
        this.supplier = new SimpleObjectProperty<>(sup);
    }

    // Getters, Setters, dan Property Getters (tidak ada perubahan di sini)
    public int getBarangId() { return barangId.get(); }
    public IntegerProperty barangIdProperty() { return barangId; }
    public String getNamaBarang() { return namaBarang.get(); }
    public StringProperty namaBarangProperty() { return namaBarang; }
    public void setNamaBarang(String nama) { this.namaBarang.set(nama); }
    public String getDeskripsi() { return deskripsi.get(); }
    public StringProperty deskripsiProperty() { return deskripsi; }
    public void setDeskripsi(String desk) { this.deskripsi.set(desk); }
    public int getStok() { return stok.get(); }
    public IntegerProperty stokProperty() { return stok; }
    public void setStok(int s) { this.stok.set(s); }
    public double getHargaJual() { return hargaJual.get(); }
    public DoubleProperty hargaJualProperty() { return hargaJual; }
    public void setHargaJual(double h) { this.hargaJual.set(h); }
    public Kategori getKategori() { return kategori.get(); }
    public ObjectProperty<Kategori> kategoriProperty() { return kategori; }
    public void setKategori(Kategori k) { this.kategori.set(k); }
    public Supplier getSupplier() { return supplier.get(); }
    public ObjectProperty<Supplier> supplierProperty() { return supplier; }
    public void setSupplier(Supplier s) { this.supplier.set(s); }
    
    // --- Properti Turunan untuk TableView ---
    public StringProperty namaKategoriProperty() {
        return (kategori.get() != null) ? new SimpleStringProperty(kategori.get().getNamaKategori()) : new SimpleStringProperty("-");
    }
    public StringProperty namaSupplierProperty() {
        return (supplier.get() != null) ? new SimpleStringProperty(supplier.get().getNamaSupplier()) : new SimpleStringProperty("-");
    }
    // PROPERTI TURUNAN BARU
    public StringProperty teleponSupplierProperty() {
        return (supplier.get() != null) ? new SimpleStringProperty(supplier.get().getNomorTelepon()) : new SimpleStringProperty("");
    }
    public StringProperty alamatSupplierProperty() {
        return (supplier.get() != null) ? new SimpleStringProperty(supplier.get().getAlamat()) : new SimpleStringProperty("");
    }
}