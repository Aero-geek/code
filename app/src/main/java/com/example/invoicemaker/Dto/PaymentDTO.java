package com.example.invoicemaker.Dto;

public class PaymentDTO {
    private long catalogId;
    private long id;
    private double paidAmount;
    private String paymentDate;
    private String paymentMethod;
    private String paymentNotes;

    public long getCatalogId() {
        return this.catalogId;
    }

    public void setCatalogId(long j) {
        this.catalogId = j;
    }

    public double getPaidAmount() {
        return this.paidAmount;
    }

    public void setPaidAmount(double d) {
        this.paidAmount = d;
    }

    public String getPaymentDate() {
        return this.paymentDate;
    }

    public void setPaymentDate(String str) {
        this.paymentDate = str;
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    public void setPaymentMethod(String str) {
        this.paymentMethod = str;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PaymentDTO{catalogId=");
        stringBuilder.append(this.catalogId);
        stringBuilder.append(", paidAmount=");
        stringBuilder.append(this.paidAmount);
        stringBuilder.append(", paymentDate='");
        stringBuilder.append(this.paymentDate);
        stringBuilder.append('\'');
        stringBuilder.append(", paymentMethod='");
        stringBuilder.append(this.paymentMethod);
        stringBuilder.append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public String getPaymentNotes() {
        return this.paymentNotes;
    }

    public void setPaymentNotes(String str) {
        this.paymentNotes = str;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long j) {
        this.id = j;
    }
}
