package org.clarksnut.datasource.peru.beans;

import java.util.Date;

public class InvoiceBean {

    private String assignedId;
    private Date issueDate;
    private String currency;

    private SupplierBean supplier;
    private CustomerBean customer;

    private LineBean lines;

    private Float totalGravada;
    private Float totalGratuita;
    private Float totalExonerada;
    private Float totalInafecta;

    private Float totalDescuentoGlobal;
    private Float totalOtrosCargos;

    private Float totalIgv;
    private Float totalVenta;

    public String getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public SupplierBean getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierBean supplier) {
        this.supplier = supplier;
    }

    public CustomerBean getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerBean customer) {
        this.customer = customer;
    }

    public LineBean getLines() {
        return lines;
    }

    public void setLines(LineBean lines) {
        this.lines = lines;
    }

    public Float getTotalGravada() {
        return totalGravada;
    }

    public void setTotalGravada(Float totalGravada) {
        this.totalGravada = totalGravada;
    }

    public Float getTotalGratuita() {
        return totalGratuita;
    }

    public void setTotalGratuita(Float totalGratuita) {
        this.totalGratuita = totalGratuita;
    }

    public Float getTotalExonerada() {
        return totalExonerada;
    }

    public void setTotalExonerada(Float totalExonerada) {
        this.totalExonerada = totalExonerada;
    }

    public Float getTotalInafecta() {
        return totalInafecta;
    }

    public void setTotalInafecta(Float totalInafecta) {
        this.totalInafecta = totalInafecta;
    }

    public Float getTotalDescuentoGlobal() {
        return totalDescuentoGlobal;
    }

    public void setTotalDescuentoGlobal(Float totalDescuentoGlobal) {
        this.totalDescuentoGlobal = totalDescuentoGlobal;
    }

    public Float getTotalOtrosCargos() {
        return totalOtrosCargos;
    }

    public void setTotalOtrosCargos(Float totalOtrosCargos) {
        this.totalOtrosCargos = totalOtrosCargos;
    }

    public Float getTotalIgv() {
        return totalIgv;
    }

    public void setTotalIgv(Float totalIgv) {
        this.totalIgv = totalIgv;
    }

    public Float getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(Float totalVenta) {
        this.totalVenta = totalVenta;
    }
}
