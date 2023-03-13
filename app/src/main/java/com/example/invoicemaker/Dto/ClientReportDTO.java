package com.example.invoicemaker.Dto;

public class ClientReportDTO {
    private long id;
    private int invoices;
    private String name;
    private double paidAmount;

    public long getId() {
        return this.id;
    }

    public void setId(long j) {
        this.id = j;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public int getInvoices() {
        return this.invoices;
    }

    public void setInvoices(int i) {
        this.invoices = i;
    }

    public double getPaidAmount() {
        return this.paidAmount;
    }

    public void setPaidAmount(double d) {
        this.paidAmount = d;
    }
}
