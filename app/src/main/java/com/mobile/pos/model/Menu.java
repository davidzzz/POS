package com.mobile.pos.model;

public class Menu {
    private String nama, kode, keterangan, uom, wh, printCode;
    private float harga;

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

    public void setHarga(float harga) {
        this.harga = harga;
    }

    public float getHarga() {
        return harga;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getUom() {
        return uom;
    }

    public void setWh(String wh) {
        this.wh = wh;
    }

    public String getWh() {
        return wh;
    }

    public void setPrintCode(String printCode) {
        this.printCode = printCode;
    }

    public String getPrintCode() {
        return printCode;
    }
}
