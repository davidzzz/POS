package com.mobile.pos.model;

public class Spec {
    private String kode, status;
    private boolean ktv;

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setKtv(boolean ktv) {
        this.ktv = ktv;
    }

    public boolean isKtv() {
        return ktv;
    }

    @Override
    public String toString() {
        return getKode();
    }
}
