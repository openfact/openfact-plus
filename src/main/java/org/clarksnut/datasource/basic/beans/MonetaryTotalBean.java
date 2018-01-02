package org.clarksnut.datasource.basic.beans;

public class MonetaryTotalBean {

    private Float payableAmount;
    private Float allowanceTotal;
    private Float chargeTotal;

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
}
