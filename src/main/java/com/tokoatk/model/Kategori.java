package com.tokoatk.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Kategori {
    private final IntegerProperty kategoriId;
    private final StringProperty namaKategori;

    public Kategori(int id, String nama) {
        this.kategoriId = new SimpleIntegerProperty(id);
        this.namaKategori = new SimpleStringProperty(nama);
    }

    public int getKategoriId() { return kategoriId.get(); }
    public IntegerProperty kategoriIdProperty() { return kategoriId; }

    public String getNamaKategori() { return namaKategori.get(); }
    public StringProperty namaKategoriProperty() { return namaKategori; }

    @Override
    public String toString() {
        return getNamaKategori();
    }
}