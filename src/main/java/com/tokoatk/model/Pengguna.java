package com.tokoatk.model;

/**
 * Model Class (PBO) untuk menyimpan data pengguna yang sedang login.
 */
public class Pengguna {
    
    private int penggunaId;
    private String username;
    private String namaLengkap;

    // Konstruktor
    public Pengguna(int penggunaId, String username, String namaLengkap) {
        this.penggunaId = penggunaId;
        this.username = username;
        this.namaLengkap = namaLengkap;
    }

    // --- Getters ---
    
    public int getPenggunaId() {
        return penggunaId;
    }

    public String getUsername() {
        return username;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }
}