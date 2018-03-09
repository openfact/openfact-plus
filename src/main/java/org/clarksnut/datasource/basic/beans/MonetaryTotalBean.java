package org.clarksnut.datasource.basic.beans;

public class MonetaryTotalBean {

    private Double payableAmount;
    private Double allowanceTotal;
    private Double chargeTotal;

    public Double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(Double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public Double getAllowanceTotal() {
        return allowanceTotal;
    }

    public void setAllowanceTotal(Double allowanceTotal) {
        this.allowanceTotal = allowanceTotal;
    }

    public Double getChargeTotal() {
        return chargeTotal;
    }

    public void setChargeTotal(Double chargeTotal) {
        this.chargeTotal = chargeTotal;
    }
}
