package com.mobile.pos.model;

public class Opsi {
    private String taxCal;
    private float tax, service;

    public void setTaxCal(String taxCal) {
        this.taxCal = taxCal;
    }

    public String getTaxCal() {
        return taxCal;
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
}
