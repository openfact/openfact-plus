package org.clarksnut.datasource.peru.beans;

public class SummaryLineBean {

    private String tipoDocumento;
    private String documentoSerie;
    private String documentoNumeroInicio;
    private String documentoNumeroFin;

    private InformacionAdicionalBean informacionAdicional;
    private TributosBean tributos;

    private Double totalVenta;
    private Double totalOtrosCargos;

    private String moneda;

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumentoSerie() {
        return documentoSerie;
    }

    public void setDocumentoSerie(String documentoSerie) {
        this.documentoSerie = documentoSerie;
    }

    public String getDocumentoNumeroInicio() {
        return documentoNumeroInicio;
    }

    public void setDocumentoNumeroInicio(String documentoNumeroInicio) {
        this.documentoNumeroInicio = documentoNumeroInicio;
    }

    public String getDocumentoNumeroFin() {
        return documentoNumeroFin;
    }

    public void setDocumentoNumeroFin(String documentoNumeroFin) {
        this.documentoNumeroFin = documentoNumeroFin;
    }

    public InformacionAdicionalBean getInformacionAdicional() {
        return informacionAdicional;
    }

    public void setInformacionAdicional(InformacionAdicionalBean informacionAdicional) {
        this.informacionAdicional = informacionAdicional;
    }

    public TributosBean getTributos() {
        return tributos;
    }

    public void setTributos(TributosBean tributos) {
        this.tributos = tributos;
    }

    public Double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(Double totalVenta) {
        this.totalVenta = totalVenta;
    }

    public Double getTotalOtrosCargos() {
        return totalOtrosCargos;
    }

    public void setTotalOtrosCargos(Double totalOtrosCargos) {
        this.totalOtrosCargos = totalOtrosCargos;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
}
