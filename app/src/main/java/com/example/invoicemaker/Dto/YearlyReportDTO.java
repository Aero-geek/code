package com.example.invoicemaker.Dto;

public class YearlyReportDTO {
    private int totalClients;
    private int totalInvoices;
    private double totalPaidAmount;
    private int year;

    public int getYear() {
        return this.year;
    }

    public void setYear(int i) {
        this.year = i;
    }

    public int getTotalClients() {
        return this.totalClients;
    }

    public void setTotalClients(int i) {
        this.totalClients = i;
    }

    public int getTotalInvoices() {
        return this.totalInvoices;
    }

    public void setTotalInvoices(int i) {
        this.totalInvoices = i;
    }

    public double getTotalPaidAmount() {
        return this.totalPaidAmount;
    }

    public void setTotalPaidAmount(double d) {
        this.totalPaidAmount = d;
    }
}
