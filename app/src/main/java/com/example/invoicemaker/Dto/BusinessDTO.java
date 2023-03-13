package com.example.invoicemaker.Dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BusinessDTO implements Serializable {
    @SerializedName("bank_information")
    private String bankInformation;
    @SerializedName("check_information")
    private String checkInformation;
    @SerializedName("email")
    private String email;
    @SerializedName("fax")
    private String fax;
    @SerializedName("id")
    private long id;
    @SerializedName("line1")
    private String line1;
    @SerializedName("line2")
    private String line2;
    @SerializedName("line3")
    private String line3;
    @SerializedName("logo")
    private String logo;
    @SerializedName("mobile_no")
    private String mobileNo;
    @SerializedName("name")
    private String name;
    @SerializedName("other_payment_information")
    private String otherPaymentInformation;
    @SerializedName("paypal_address")
    private String paypalAddress;
    @SerializedName("phone_no")
    private String phoneNo;
    @SerializedName("reg_no")
    private String regNo;
    @SerializedName("website")
    private String website;
    @SerializedName("signed_date")
    private String signedDate;
    @SerializedName("signedUrl")
    private String signedUrl = "";

    public String getSignedDate() {
        return signedDate;
    }

    public void setSignedDate(String signedDate) {
        this.signedDate = signedDate;
    }

    public String getSignedUrl() {
        return signedUrl;
    }

    public void setSignedUrl(String signedUrl) {
        this.signedUrl = signedUrl;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getRegNo() {
        return this.regNo;
    }

    public void setRegNo(String str) {
        this.regNo = str;
    }

    public String getLine1() {
        return this.line1;
    }

    public void setLine1(String str) {
        this.line1 = str;
    }

    public String getLine2() {
        return this.line2;
    }

    public void setLine2(String str) {
        this.line2 = str;
    }

    public String getLine3() {
        return this.line3;
    }

    public void setLine3(String str) {
        this.line3 = str;
    }

    public String getPhoneNo() {
        return this.phoneNo;
    }

    public void setPhoneNo(String str) {
        this.phoneNo = str;
    }

    public String getMobileNo() {
        return this.mobileNo;
    }

    public void setMobileNo(String str) {
        this.mobileNo = str;
    }

    public String getFax() {
        return this.fax;
    }

    public void setFax(String str) {
        this.fax = str;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String str) {
        this.email = str;
    }

    public String getWebsite() {
        return this.website;
    }

    public void setWebsite(String str) {
        this.website = str;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String str) {
        this.logo = str;
    }

    public String getBankInformation() {
        return this.bankInformation;
    }

    public void setBankInformation(String str) {
        this.bankInformation = str;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long j) {
        this.id = j;
    }

    public String getPaypalAddress() {
        return this.paypalAddress;
    }

    public void setPaypalAddress(String str) {
        this.paypalAddress = str;
    }

    public String getCheckInformation() {
        return this.checkInformation;
    }

    public void setCheckInformation(String str) {
        this.checkInformation = str;
    }

    public String getOtherPaymentInformation() {
        return this.otherPaymentInformation;
    }

    public void setOtherPaymentInformation(String str) {
        this.otherPaymentInformation = str;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("BusinessDTO{id=");
        stringBuilder.append(this.id);
        stringBuilder.append(", paypalAddress='");
        stringBuilder.append(this.paypalAddress);
        stringBuilder.append('\'');
        stringBuilder.append(", checkInformation='");
        stringBuilder.append(this.checkInformation);
        stringBuilder.append('\'');
        stringBuilder.append(", bankInformation='");
        stringBuilder.append(this.bankInformation);
        stringBuilder.append('\'');
        stringBuilder.append(", otherPaymentInformation='");
        stringBuilder.append(this.otherPaymentInformation);
        stringBuilder.append('\'');
        stringBuilder.append(", name='");
        stringBuilder.append(this.name);
        stringBuilder.append('\'');
        stringBuilder.append(", regNo='");
        stringBuilder.append(this.regNo);
        stringBuilder.append('\'');
        stringBuilder.append(", line1='");
        stringBuilder.append(this.line1);
        stringBuilder.append('\'');
        stringBuilder.append(", line2='");
        stringBuilder.append(this.line2);
        stringBuilder.append('\'');
        stringBuilder.append(", line3='");
        stringBuilder.append(this.line3);
        stringBuilder.append('\'');
        stringBuilder.append(", phoneNo='");
        stringBuilder.append(this.phoneNo);
        stringBuilder.append('\'');
        stringBuilder.append(", mobileNo='");
        stringBuilder.append(this.mobileNo);
        stringBuilder.append('\'');
        stringBuilder.append(", fax='");
        stringBuilder.append(this.fax);
        stringBuilder.append('\'');
        stringBuilder.append(", email='");
        stringBuilder.append(this.email);
        stringBuilder.append('\'');
        stringBuilder.append(", website='");
        stringBuilder.append(this.website);
        stringBuilder.append('\'');
        stringBuilder.append(", logo='");
        stringBuilder.append(this.logo);
        stringBuilder.append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
