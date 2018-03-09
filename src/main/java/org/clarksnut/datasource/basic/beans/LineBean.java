package org.clarksnut.datasource.basic.beans;

public class LineBean {

    private Double quantity;
    private String unitCode;
    private String description;
    private String productCode;
    private Double totalTax;
    private Double totalAllowanceCharge;
    private Double extensionAmount;
    private Double priceAmount;

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Double getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(Double totalTax) {
        this.totalTax = totalTax;
    }

    public Double getTotalAllowanceCharge() {
        return totalAllowanceCharge;
    }

    public void setTotalAllowanceCharge(Double totalAllowanceCharge) {
        this.totalAllowanceCharge = totalAllowanceCharge;
    }

    public Double getExtensionAmount() {
        return extensionAmount;
    }

    public void setExtensionAmount(Double extensionAmount) {
        this.extensionAmount = extensionAmount;
    }

    public Double getPriceAmount() {
        return priceAmount;
    }

    public void setPriceAmount(Double priceAmount) {
        this.priceAmount = priceAmount;
    }
}
