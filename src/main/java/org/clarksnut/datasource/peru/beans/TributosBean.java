package org.clarksnut.datasource.peru.beans;

public class TributosBean {

    private Float totalIgv;
    private Float totalIsc;
    private Float totalOtrosTributos;

    public Float getTotalIgv() {
        return totalIgv;
    }

    public void setTotalIgv(Float totalIgv) {
        this.totalIgv = totalIgv;
    }

    public Float getTotalIsc() {
        return totalIsc;
    }

    public void setTotalIsc(Float totalIsc) {
        this.totalIsc = totalIsc;
    }

    public Float getTotalOtrosTributos() {
        return totalOtrosTributos;
    }

    public void setTotalOtrosTributos(Float totalOtrosTributos) {
        this.totalOtrosTributos = totalOtrosTributos;
    }
}
