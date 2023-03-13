package com.example.invoicemaker.Dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ImageDTO implements Serializable {
    @SerializedName("additional_details")
    private String additionalDetails;
    @SerializedName("catalog_id")
    private long catalogId;
    @SerializedName("description")
    private String description;
    @SerializedName("id")
    private long id;
    @SerializedName("img_url")
    private String imageUrl;

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

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String str) {
        this.imageUrl = str;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public String getAdditionalDetails() {
        return this.additionalDetails;
    }

    public void setAdditionalDetails(String str) {
        this.additionalDetails = str;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ImageDTO{id=");
        stringBuilder.append(this.id);
        stringBuilder.append(", catalogId=");
        stringBuilder.append(this.catalogId);
        stringBuilder.append(", imageUrl='");
        stringBuilder.append(this.imageUrl);
        stringBuilder.append('\'');
        stringBuilder.append(", description='");
        stringBuilder.append(this.description);
        stringBuilder.append('\'');
        stringBuilder.append(", additionalDetails='");
        stringBuilder.append(this.additionalDetails);
        stringBuilder.append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
