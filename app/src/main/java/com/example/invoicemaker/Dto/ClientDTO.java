package com.example.invoicemaker.Dto;

import java.io.Serializable;

public class ClientDTO implements Serializable {
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private long catalogId;
    private String clientName;
    private String contactAdress;
    private double dueAmount;
    private String emailAddress;
    private String faxNo;
    private long id;
    private String invoiceDate;
    private String mobileNo;
    private String phoneNo;
    private String shippingAddress;
    private String shippingLine1;
    private String shippingLine2;
    private String shippingLine3;
    private double totalAmount;
    private double totalAmountEstimate;
    private int totalEstimate;
    private int totalInvoice;

    public long getCatalogId() {
        return this.catalogId;
    }

    public void setCatalogId(long j) {
        this.catalogId = j;
    }

    public int getTotalInvoice() {
        return this.totalInvoice;
    }

    public void setTotalInvoice(int i) {
        this.totalInvoice = i;
    }

    public int getTotalEstimate() {
        return this.totalEstimate;
    }

    public void setTotalEstimate(int i) {
        this.totalEstimate = i;
    }

    public double getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(double d) {
        this.totalAmount = d;
    }

    public double getTotalAmountEstimate() {
        return this.totalAmountEstimate;
    }

    public void setTotalAmountEstimate(double d) {
        this.totalAmountEstimate = d;
    }

    public double getDueAmount() {
        return this.dueAmount;
    }

    public void setDueAmount(double d) {
        this.dueAmount = d;
    }

    public String getInvoiceDate() {
        return this.invoiceDate;
    }

    public void setInvoiceDate(String str) {
        this.invoiceDate = str;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long j) {
        this.id = j;
    }

    public String getClientName() {
        return this.clientName;
    }

    public void setClientName(String str) {
        this.clientName = str;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String str) {
        this.emailAddress = str;
    }

    public String getMobileNo() {
        return this.mobileNo;
    }

    public void setMobileNo(String str) {
        this.mobileNo = str;
    }

    public String getPhoneNo() {
        return this.phoneNo;
    }

    public void setPhoneNo(String str) {
        this.phoneNo = str;
    }

    public String getFaxNo() {
        return this.faxNo;
    }

    public void setFaxNo(String str) {
        this.faxNo = str;
    }

    public String getContactAdress() {
        return this.contactAdress;
    }

    public void setContactAdress(String str) {
        this.contactAdress = str;
    }

    public String getAddressLine1() {
        return this.addressLine1;
    }

    public void setAddressLine1(String str) {
        this.addressLine1 = str;
    }

    public String getAddressLine2() {
        return this.addressLine2;
    }

    public void setAddressLine2(String str) {
        this.addressLine2 = str;
    }

    public String getAddressLine3() {
        return this.addressLine3;
    }

    public void setAddressLine3(String str) {
        this.addressLine3 = str;
    }

    public String getShippingAddress() {
        return this.shippingAddress;
    }

    public void setShippingAddress(String str) {
        this.shippingAddress = str;
    }

    public String getShippingLine1() {
        return this.shippingLine1;
    }

    public void setShippingLine1(String str) {
        this.shippingLine1 = str;
    }

    public String getShippingLine2() {
        return this.shippingLine2;
    }

    public void setShippingLine2(String str) {
        this.shippingLine2 = str;
    }

    public String getShippingLine3() {
        return this.shippingLine3;
    }

    public void setShippingLine3(String str) {
        this.shippingLine3 = str;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ClientDTO{id=");
        stringBuilder.append(this.id);
        stringBuilder.append(", catalogId=");
        stringBuilder.append(this.catalogId);
        stringBuilder.append(", totalAmount=");
        stringBuilder.append(this.totalAmount);
        stringBuilder.append(", dueAmount=");
        stringBuilder.append(this.dueAmount);
        stringBuilder.append(", invoiceDate='");
        stringBuilder.append(this.invoiceDate);
        stringBuilder.append('\'');
        stringBuilder.append(", totalInvoice=");
        stringBuilder.append(this.totalInvoice);
        stringBuilder.append(", clientName='");
        stringBuilder.append(this.clientName);
        stringBuilder.append('\'');
        stringBuilder.append(", emailAddress='");
        stringBuilder.append(this.emailAddress);
        stringBuilder.append('\'');
        stringBuilder.append(", mobileNo='");
        stringBuilder.append(this.mobileNo);
        stringBuilder.append('\'');
        stringBuilder.append(", phoneNo='");
        stringBuilder.append(this.phoneNo);
        stringBuilder.append('\'');
        stringBuilder.append(", faxNo='");
        stringBuilder.append(this.faxNo);
        stringBuilder.append('\'');
        stringBuilder.append(", contactAdress='");
        stringBuilder.append(this.contactAdress);
        stringBuilder.append('\'');
        stringBuilder.append(", addressLine1='");
        stringBuilder.append(this.addressLine1);
        stringBuilder.append('\'');
        stringBuilder.append(", addressLine2='");
        stringBuilder.append(this.addressLine2);
        stringBuilder.append('\'');
        stringBuilder.append(", addressLine3='");
        stringBuilder.append(this.addressLine3);
        stringBuilder.append('\'');
        stringBuilder.append(", shippingAddress='");
        stringBuilder.append(this.shippingAddress);
        stringBuilder.append('\'');
        stringBuilder.append(", shippingLine1='");
        stringBuilder.append(this.shippingLine1);
        stringBuilder.append('\'');
        stringBuilder.append(", shippingLine2='");
        stringBuilder.append(this.shippingLine2);
        stringBuilder.append('\'');
        stringBuilder.append(", shippingLine3='");
        stringBuilder.append(this.shippingLine3);
        stringBuilder.append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
