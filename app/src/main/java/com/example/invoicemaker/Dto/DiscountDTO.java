package com.example.invoicemaker.Dto;

public class DiscountDTO {
    private long catalogId;
    private double discountAmount;
    private int discountType;

    public long getCatalogId() {
        return this.catalogId;
    }

    public void setCatalogId(long j) {
        this.catalogId = j;
    }

    public int getDiscountType() {
        return this.discountType;
    }

    public void setDiscountType(int i) {
        this.discountType = i;
    }

    public double getDiscountAmount() {
        return this.discountAmount;
    }

    public void setDiscountAmount(double d) {
        this.discountAmount = d;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DiscountDTO{catalogId=");
        stringBuilder.append(this.catalogId);
        stringBuilder.append(", discountType=");
        stringBuilder.append(this.discountType);
        stringBuilder.append(", discountAmount=");
        stringBuilder.append(this.discountAmount);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
