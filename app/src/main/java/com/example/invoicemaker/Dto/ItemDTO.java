package com.example.invoicemaker.Dto;

import java.io.Serializable;

public class ItemDTO implements Serializable {
    private long id;
    private String itemDescription;
    private String itemName;
    private int texable;
    private String unitCost;

    public ItemDTO() {

    }

    public ItemDTO(String str, String str2, String d, int i) {
        this.itemName = str;
        this.itemDescription = str2;
        this.unitCost = d;
        this.texable = i;
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

    public String getItemDescription() {
        return this.itemDescription;
    }

    public void setItemDescription(String str) {
        this.itemDescription = str;
    }

    public String getUnitCost() {
        return this.unitCost;
    }

    public void setUnitCost(String d) {
        this.unitCost = d;
    }

    public int getTexable() {
        return this.texable;
    }

    public void setTexable(int i) {
        this.texable = i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ItemDTO{id=");
        stringBuilder.append(this.id);
        stringBuilder.append(", itemName='");
        stringBuilder.append(this.itemName);
        stringBuilder.append('\'');
        stringBuilder.append(", itemDescription='");
        stringBuilder.append(this.itemDescription);
        stringBuilder.append('\'');
        stringBuilder.append(", unitCost=");
        stringBuilder.append(this.unitCost);
        stringBuilder.append(", texable=");
        stringBuilder.append(this.texable);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
