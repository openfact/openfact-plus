package org.clarksnut.datasource.peru.beans;

public class TributosBean {

    private Double totalIgv;
    private Double totalIsc;
    private Double totalOtrosTributos;

    public Double getTotalIgv() {
        return totalIgv;
    }

    public void setTotalIgv(Double totalIgv) {
        this.totalIgv = totalIgv;
    }

    public Double getTotalIsc() {
        return totalIsc;
    }

    public void setTotalIsc(Double totalIsc) {
        this.totalIsc = totalIsc;
    }

    public Double getTotalOtrosTributos() {
        return totalOtrosTributos;
    }

    public void setTotalOtrosTributos(Double totalOtrosTributos) {
        this.totalOtrosTributos = totalOtrosTributos;
    }
}
