package com.example.invoicemaker.Dto;

public class SignedDTO {
    private long catalogId;
    private String signedDate = "";
    private String signedUrl = "";

    public long getCatalogId() {
        return this.catalogId;
    }

    public void setCatalogId(long j) {
        this.catalogId = j;
    }

    public String getSignedDate() {
        return this.signedDate;
    }

    public void setSignedDate(String str) {
        this.signedDate = str;
    }

    public String getSignedUrl() {
        return this.signedUrl;
    }

    public void setSignedUrl(String str) {
        this.signedUrl = str;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SignedDTO{catalogId=");
        stringBuilder.append(this.catalogId);
        stringBuilder.append(", signedDate='");
        stringBuilder.append(this.signedDate);
        stringBuilder.append('\'');
        stringBuilder.append(", signedUrl='");
        stringBuilder.append(this.signedUrl);
        stringBuilder.append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
