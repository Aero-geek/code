package com.example.invoicemaker.Dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CatalogDTO implements Serializable, Cloneable {
    @SerializedName("catalog_name")
    private String catalogName;
    @SerializedName("client_dto")
    private ClientDTO clientDTO = new ClientDTO();
    @SerializedName("creation_date")
    private String creationDate;
    @SerializedName("discount")
    private double discount;
    @SerializedName("discount_amount")
    private double discountAmount;
    @SerializedName("discount_type")
    private int discountType;
    @SerializedName("due_date")
    private String dueDate;
    @SerializedName("estimate_status")
    private int estimateStatus;
    private long id;
    @SerializedName("invoice_shipping_dto")
    private InvoiceShippingDTO invoiceShippingDTO = new InvoiceShippingDTO();
    @SerializedName("notes")
    private String notes;
    @SerializedName("paid_amount")
    private double paidAmount;
    @SerializedName("paid_status")
    private int paidStatus;
    @SerializedName("po_number")
    private String poNumber;
    @SerializedName("signed_date")
    private String signedDate;
    private String signedUrl = "";
    @SerializedName("subtotal_amount")
    private double subTotalAmount;
    @SerializedName("tax_amount")
    private double taxAmount;
    @SerializedName("tax_label")
    private String taxLabel;
    @SerializedName("tax_rate")
    private double taxRate;
    @SerializedName("tax_type")
    private int taxType = 3;
    @SerializedName("terms")
    private int terms;
    @SerializedName("total_amount")
    private double totalAmount;
    @SerializedName("type")
    private int type;

    public int getPaidStatus() {
        return this.paidStatus;
    }

    public void setPaidStatus(int i) {
        this.paidStatus = i;
    }

    public String getSignedUrl() {
        return this.signedUrl;
    }

    public void setSignedUrl(String str) {
        this.signedUrl = str;
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

    public double getSubTotalAmount() {
        return this.subTotalAmount;
    }

    public void setSubTotalAmount(double d) {
        this.subTotalAmount = d;
    }

    public InvoiceShippingDTO getInvoiceShippingDTO() {
        return this.invoiceShippingDTO;
    }

    public void setInvoiceShippingDTO(InvoiceShippingDTO invoiceShippingDTO) {
        this.invoiceShippingDTO = invoiceShippingDTO;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long j) {
        this.id = j;
    }

    public ClientDTO getClientDTO() {
        return this.clientDTO;
    }

    public void setClientDTO(ClientDTO clientDTO) {
        this.clientDTO = clientDTO;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }

    public int getDiscountType() {
        return this.discountType;
    }

    public void setDiscountType(int i) {
        this.discountType = i;
    }

    public int getTaxType() {
        return this.taxType;
    }

    public void setTaxType(int i) {
        this.taxType = i;
    }

    public String getTaxLabel() {
        return this.taxLabel;
    }

    public void setTaxLabel(String str) {
        this.taxLabel = str;
    }

    public String getCatalogName() {
        return this.catalogName;
    }

    public void setCatalogName(String str) {
        this.catalogName = str;
    }

    public String getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(String str) {
        this.creationDate = str;
    }

    public int getTerms() {
        return this.terms;
    }

    public void setTerms(int i) {
        this.terms = i;
    }

    public String getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(String str) {
        this.dueDate = str;
    }

    public String getPoNumber() {
        return this.poNumber;
    }

    public void setPoNumber(String str) {
        this.poNumber = str;
    }

    public double getDiscountAmount() {
        return this.discountAmount;
    }

    public void setDiscountAmount(double d) {
        this.discountAmount = d;
    }

    public double getTaxAmount() {
        return this.taxAmount;
    }

    public void setTaxAmount(double d) {
        this.taxAmount = d;
    }

    public double getPaidAmount() {
        return this.paidAmount;
    }

    public void setPaidAmount(double d) {
        this.paidAmount = d;
    }

    public double getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(double d) {
        this.totalAmount = d;
    }

    public String getSignedDate() {
        return this.signedDate;
    }

    public void setSignedDate(String str) {
        this.signedDate = str;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String str) {
        this.notes = str;
    }

    public int getEstimateStatus() {
        return this.estimateStatus;
    }

    public void setEstimateStatus(int i) {
        this.estimateStatus = i;
    }

    public CatalogDTO clone() throws CloneNotSupportedException {
        return (CatalogDTO) super.clone();
    }
}
