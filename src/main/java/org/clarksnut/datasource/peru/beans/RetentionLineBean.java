package org.clarksnut.datasource.peru.beans;

import java.util.Date;

public class RetentionLineBean {

    private String documentoRelacionado;
    private String tipoDocumentoRelacionado;
    private Date fechaEmisionDocumentoRelacionado;
    private Float importeTotalDocumentoRelacionado;
    private String monedaDocumentoRelacionado;

    private Date fechaPago;
    private Float importePagoSinRetencion;
    private String monedaPagoSinRetencion;

    private Float importeRetenido;
    private String monedaImporteRetenido;
    private Date fechaRetencion;
    private Float importeTotalAPagar;
    private String monedaImporteTotalAPagar;

    private String monedaReferencia;
    private String monedaObjetivo;
    private Float tipoCambio;
    private Date fechaCambio;

    public String getDocumentoRelacionado() {
        return documentoRelacionado;
    }

    public void setDocumentoRelacionado(String documentoRelacionado) {
        this.documentoRelacionado = documentoRelacionado;
    }

    public String getTipoDocumentoRelacionado() {
        return tipoDocumentoRelacionado;
    }

    public void setTipoDocumentoRelacionado(String tipoDocumentoRelacionado) {
        this.tipoDocumentoRelacionado = tipoDocumentoRelacionado;
    }

    public Date getFechaEmisionDocumentoRelacionado() {
        return fechaEmisionDocumentoRelacionado;
    }

    public void setFechaEmisionDocumentoRelacionado(Date fechaEmisionDocumentoRelacionado) {
        this.fechaEmisionDocumentoRelacionado = fechaEmisionDocumentoRelacionado;
    }

    public Float getImporteTotalDocumentoRelacionado() {
        return importeTotalDocumentoRelacionado;
    }

    public void setImporteTotalDocumentoRelacionado(Float importeTotalDocumentoRelacionado) {
        this.importeTotalDocumentoRelacionado = importeTotalDocumentoRelacionado;
    }

    public String getMonedaDocumentoRelacionado() {
        return monedaDocumentoRelacionado;
    }

    public void setMonedaDocumentoRelacionado(String monedaDocumentoRelacionado) {
        this.monedaDocumentoRelacionado = monedaDocumentoRelacionado;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public Float getImportePagoSinRetencion() {
        return importePagoSinRetencion;
    }

    public void setImportePagoSinRetencion(Float importePagoSinRetencion) {
        this.importePagoSinRetencion = importePagoSinRetencion;
    }

    public String getMonedaPagoSinRetencion() {
        return monedaPagoSinRetencion;
    }

    public void setMonedaPagoSinRetencion(String monedaPagoSinRetencion) {
        this.monedaPagoSinRetencion = monedaPagoSinRetencion;
    }

    public Float getImporteRetenido() {
        return importeRetenido;
    }

    public void setImporteRetenido(Float importeRetenido) {
        this.importeRetenido = importeRetenido;
    }

    public String getMonedaImporteRetenido() {
        return monedaImporteRetenido;
    }

    public void setMonedaImporteRetenido(String monedaImporteRetenido) {
        this.monedaImporteRetenido = monedaImporteRetenido;
    }

    public Date getFechaRetencion() {
        return fechaRetencion;
    }

    public void setFechaRetencion(Date fechaRetencion) {
        this.fechaRetencion = fechaRetencion;
    }

    public Float getImporteTotalAPagar() {
        return importeTotalAPagar;
    }

    public void setImporteTotalAPagar(Float importeTotalAPagar) {
        this.importeTotalAPagar = importeTotalAPagar;
    }

    public String getMonedaImporteTotalAPagar() {
        return monedaImporteTotalAPagar;
    }

    public void setMonedaImporteTotalAPagar(String monedaImporteTotalAPagar) {
        this.monedaImporteTotalAPagar = monedaImporteTotalAPagar;
    }

    public String getMonedaReferencia() {
        return monedaReferencia;
    }

    public void setMonedaReferencia(String monedaReferencia) {
        this.monedaReferencia = monedaReferencia;
    }

    public String getMonedaObjetivo() {
        return monedaObjetivo;
    }

    public void setMonedaObjetivo(String monedaObjetivo) {
        this.monedaObjetivo = monedaObjetivo;
    }

    public Float getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(Float tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public Date getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(Date fechaCambio) {
        this.fechaCambio = fechaCambio;
    }
}
