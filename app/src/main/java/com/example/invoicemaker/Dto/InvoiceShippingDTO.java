package com.example.invoicemaker.Dto;

import java.io.Serializable;

public class InvoiceShippingDTO implements Serializable {
    private double amount;
    private long catalogId;
    private String fob;
    private long id;
    private String shipVia;
    private String shippingDate;
    private String tracking;

    public long getId() {
        return this.id;
    }

    public void setId(long j) {
        this.id = j;
    }

    public long getCatalogId() {
        return this.catalogId;
    }

    public void setCatalogId(long j) {
        this.catalogId = j;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double d) {
        this.amount = d;
    }

    public String getShippingDate() {
        return this.shippingDate;
    }

    public void setShippingDate(String str) {
        this.shippingDate = str;
    }

    public String getShipVia() {
        return this.shipVia;
    }

    public void setShipVia(String str) {
        this.shipVia = str;
    }

    public String getTracking() {
        return this.tracking;
    }

    public void setTracking(String str) {
        this.tracking = str;
    }

    public String getFob() {
        return this.fob;
    }

    public void setFob(String str) {
        this.fob = str;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("InvoiceShippingDTO{id=");
        stringBuilder.append(this.id);
        stringBuilder.append(", catalogId=");
        stringBuilder.append(this.catalogId);
        stringBuilder.append(", amount=");
        stringBuilder.append(this.amount);
        stringBuilder.append(", shippingDate='");
        stringBuilder.append(this.shippingDate);
        stringBuilder.append('\'');
        stringBuilder.append(", shipVia='");
        stringBuilder.append(this.shipVia);
        stringBuilder.append('\'');
        stringBuilder.append(", tracking='");
        stringBuilder.append(this.tracking);
        stringBuilder.append('\'');
        stringBuilder.append(", fob='");
        stringBuilder.append(this.fob);
        stringBuilder.append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
