package com.tokoatk.model;

import javafx.beans.property.*;

public class Barang {
    private final IntegerProperty barangId;
    private final StringProperty namaBarang;
    private final StringProperty deskripsi;
    private final IntegerProperty stok;
    private final DoubleProperty hargaJual;
    private final ObjectProperty<Kategori> kategori; 

    public Barang(int id, String nama, String desk, int stok, double harga, Kategori kat) {
        this.barangId = new SimpleIntegerProperty(id);
        this.namaBarang = new SimpleStringProperty(nama);
        this.deskripsi = new SimpleStringProperty(desk);
        this.stok = new SimpleIntegerProperty(stok);
        this.hargaJual = new SimpleDoubleProperty(harga);
        this.kategori = new SimpleObjectProperty<>(kat);
    }

    // Getters, Setters, dan Property Getters
    
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
    
    // Properti turunan untuk TableView
    public StringProperty namaKategoriProperty() {
        if (kategori.get() != null) {
            return new SimpleStringProperty(kategori.get().getNamaKategori());
        } else {
            return new SimpleStringProperty("-");
        }
    }
}