package com.mobile.pos.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable {
    private String nama, kode, keterangan, uom, wh, printCode;
    private float harga, tax, service;
    private int qty;

    public Order() {}

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

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getQty() {
        return qty;
    }

    public float getSubtotal() {
        return qty * harga;
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

    public void setTax(float tax) {
        this.tax = tax;
    }

    public float getTax() {
        return tax;
    }

    public void setService(float service) {
        this.service = service;
    }

    public float getService() {
        return service;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(nama);
        out.writeString(kode);
        out.writeString(keterangan);
        out.writeString(uom);
        out.writeString(wh);
        out.writeString(printCode);
        out.writeFloat(harga);
        out.writeInt(qty);
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    private Order(Parcel in) {
        nama = in.readString();
        kode = in.readString();
        keterangan = in.readString();
        uom = in.readString();
        wh = in.readString();
        printCode = in.readString();
        harga = in.readFloat();
        qty = in.readInt();
    }
}
