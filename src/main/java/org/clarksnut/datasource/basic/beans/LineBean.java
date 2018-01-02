package org.clarksnut.datasource.basic.beans;

public class LineBean {

    private Float quantity;
    private String unitCode;
    private String description;
    private String productCode;
    private Float totalTax;
    private Float totalAllowanceCharge;
    private float extensionAmount;
    private float priceAmount;

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
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

    public void setTotalTax(Float totalTax) {
        this.totalTax = totalTax;
    }

    public Float getTotalTax() {
        return totalTax;
    }

    public void setTotalAllowanceCharge(Float totalAllowanceCharge) {
        this.totalAllowanceCharge = totalAllowanceCharge;
    }

    public Float getTotalAllowanceCharge() {
        return totalAllowanceCharge;
    }

    public void setExtensionAmount(float extensionAmount) {
        this.extensionAmount = extensionAmount;
    }

    public float getExtensionAmount() {
        return extensionAmount;
    }

    public void setPriceAmount(float priceAmount) {
        this.priceAmount = priceAmount;
    }

    public float getPriceAmount() {
        return priceAmount;
    }
}
