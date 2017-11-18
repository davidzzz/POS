package com.mobile.pos.model;

public class Kategori {
    public String nama, kode;

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }

    @Override
    public String toString() {
        return getNama();
    }
}
