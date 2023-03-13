package com.example.invoicemaker.Dto;

public class TaxDTO {
    private long catalogId;
    private String taxLabel;
    private double taxRate;
    private int taxType;

    public int getTaxType() {
        return this.taxType;
    }

    public void setTaxType(int i) {
        this.taxType = i;
    }

    public long getCatalogId() {
        return this.catalogId;
    }

    public void setCatalogId(long j) {
        this.catalogId = j;
    }

    public double getTaxRate() {
        return this.taxRate;
    }

    public void setTaxRate(double d) {
        this.taxRate = d;
    }

    public String getTaxLabel() {
        return this.taxLabel;
    }

    public void setTaxLabel(String str) {
        this.taxLabel = str;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("TaxDTO{catalogId=");
        stringBuilder.append(this.catalogId);
        stringBuilder.append(", taxType=");
        stringBuilder.append(this.taxType);
        stringBuilder.append(", taxRate=");
        stringBuilder.append(this.taxRate);
        stringBuilder.append(", taxLabel='");
        stringBuilder.append(this.taxLabel);
        stringBuilder.append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
