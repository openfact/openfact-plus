package org.clarksnut.datasource.basic;

import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.basic.beans.*;

import java.util.Date;
import java.util.List;

public class BasicInvoiceDatasource implements Datasource {

    private Date issueDate;
    private String currency;
    private String assignedId;

    private PostalAddressBean deliveryAddress;

    private SupplierBean supplier;
    private CustomerBean customer;
    private MonetaryTotalBean monetaryTotal;

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

    public PostalAddressBean getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(PostalAddressBean deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
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

    public MonetaryTotalBean getMonetaryTotal() {
        return monetaryTotal;
    }

    public void setMonetaryTotal(MonetaryTotalBean monetaryTotal) {
        this.monetaryTotal = monetaryTotal;
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
