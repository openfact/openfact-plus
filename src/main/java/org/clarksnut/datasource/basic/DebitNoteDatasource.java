package org.clarksnut.datasource.basic;

import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.basic.beans.CustomerBean;
import org.clarksnut.datasource.basic.beans.LineBean;
import org.clarksnut.datasource.basic.beans.SupplierBean;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DebitNoteDatasource extends Datasource {

    private Date issueDate;
    private String currency;
    private String assignedId;

    private String invoiceReference;

    private SupplierBean supplier;
    private CustomerBean customer;

    private String note;

    private Float payableAmount;
    private Float allowanceTotal;
    private Float chargeTotal;

    private Float totalTax;
    private List<LineBean> lines;

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

    public String getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

    public String getInvoiceReference() {
        return invoiceReference;
    }

    public void setInvoiceReference(String invoiceReference) {
        this.invoiceReference = invoiceReference;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Float getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(Float payableAmount) {
        this.payableAmount = payableAmount;
    }

    public Float getAllowanceTotal() {
        return allowanceTotal;
    }

    public void setAllowanceTotal(Float allowanceTotal) {
        this.allowanceTotal = allowanceTotal;
    }

    public Float getChargeTotal() {
        return chargeTotal;
    }

    public void setChargeTotal(Float chargeTotal) {
        this.chargeTotal = chargeTotal;
    }

    public Float getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(Float totalTax) {
        this.totalTax = totalTax;
    }

    public List<LineBean> getLines() {
        return lines;
    }

    public void setLines(List<LineBean> lines) {
        this.lines = lines;
    }
}
