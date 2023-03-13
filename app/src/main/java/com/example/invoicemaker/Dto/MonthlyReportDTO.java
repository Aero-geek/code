package com.example.invoicemaker.Dto;

import java.util.HashSet;
import java.util.Set;

public class MonthlyReportDTO {
    private Set<Long> clientList;
    private int totalClientsPerYear;
    private int totalInvoices;
    private double totalPaidAmount;
    private int yearOrMonth;

    public int getYearOrMonth() {
        return this.yearOrMonth;
    }

    public void setYearOrMonth(int i) {
        this.yearOrMonth = i;
    }

    public long getTotalClientsPerYear() {
        return (long) this.totalClientsPerYear;
    }

    public void setTotalClientsPerYear(int i) {
        this.totalClientsPerYear = i;
    }

    public long getTotalClients() {
        if (this.clientList == null) {
            return 0;
        }
        return (long) this.clientList.size();
    }

    public void setClients(long j) {
        if (this.clientList == null) {
            this.clientList = new HashSet();
        }
        this.clientList.add(Long.valueOf(j));
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
