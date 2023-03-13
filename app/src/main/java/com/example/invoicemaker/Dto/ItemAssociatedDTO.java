package com.example.invoicemaker.Dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemAssociatedDTO implements Serializable {
    @SerializedName("catalog_id")
    private long catalogId;
    @SerializedName("description")
    private String description;
    @SerializedName("discount")
    private double discount;
    @SerializedName("id")
    private long id;
    @SerializedName("item_name")
    private String itemName;
    @SerializedName("quantity")
    private double quantity = 1.0d;
    @SerializedName("tax_able")
    private int taxAble;
    @SerializedName("tax_rate")
    private double taxRate;
    private String totalAmount;
    @SerializedName("unit_cost")
    private String unitCost;

    public String getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(String d) {
        this.totalAmount = d;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long j) {
        this.id = j;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String str) {
        this.itemName = str;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public String getUnitCost() {
        return this.unitCost;
    }

    public void setUnitCost(String d) {
        this.unitCost = d;
    }

    public int getTaxAble() {
        return this.taxAble;
    }

    public void setTaxAble(int i) {
        this.taxAble = i;
    }

    public long getCatalogId() {
        return this.catalogId;
    }

    public void setCatalogId(long j) {
        this.catalogId = j;
    }

    public double getQuantity() {
        return this.quantity;
    }

    public void setQuantity(double d) {
        this.quantity = d;
    }

    public double getTaxRate() {
        return this.taxRate;
    }

    public void setTaxRate(double d) {
        this.taxRate = d;
    }

    public double getDiscount() {
        return this.discount;
    }

    public void setDiscount(double d) {
        this.discount = d;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ItemAssociatedDTO{id=");
        stringBuilder.append(this.id);
        stringBuilder.append(", itemName='");
        stringBuilder.append(this.itemName);
        stringBuilder.append('\'');
        stringBuilder.append(", description='");
        stringBuilder.append(this.description);
        stringBuilder.append('\'');
        stringBuilder.append(", unitCost=");
        stringBuilder.append(this.unitCost);
        stringBuilder.append(", taxAble=");
        stringBuilder.append(this.taxAble);
        stringBuilder.append(", catalogId=");
        stringBuilder.append(this.catalogId);
        stringBuilder.append(", quantity=");
        stringBuilder.append(this.quantity);
        stringBuilder.append(", taxRate=");
        stringBuilder.append(this.taxRate);
        stringBuilder.append(", discount=");
        stringBuilder.append(this.discount);
        stringBuilder.append(", totalAmount=");
        stringBuilder.append(this.totalAmount);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
