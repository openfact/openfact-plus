package org.clarksnut.datasource.peru.beans;

import java.util.Date;

public class PerceptionLineBean {

    private String documentoRelacionado;
    private String tipoDocumentoRelacionado;
    private Date fechaEmisionDocumentoRelacionado;
    private Float importeTotalDocumentoRelacionado;
    private String monedaDocumentoRelacionado;

    private Date fechaCobro;
    private Float importeCobro;
    private String monedaCobro;

    private Float importePercibido;
    private String monedaImportePercibido;
    private Date fechaPercepcion;
    private Float importeTotalACobrar;
    private String monedaImporteTotalACobrar;

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

    public Date getFechaCobro() {
        return fechaCobro;
    }

    public void setFechaCobro(Date fechaCobro) {
        this.fechaCobro = fechaCobro;
    }

    public Float getImporteCobro() {
        return importeCobro;
    }

    public void setImporteCobro(Float importeCobro) {
        this.importeCobro = importeCobro;
    }

    public String getMonedaCobro() {
        return monedaCobro;
    }

    public void setMonedaCobro(String monedaCobro) {
        this.monedaCobro = monedaCobro;
    }

    public Float getImportePercibido() {
        return importePercibido;
    }

    public void setImportePercibido(Float importePercibido) {
        this.importePercibido = importePercibido;
    }

    public String getMonedaImportePercibido() {
        return monedaImportePercibido;
    }

    public void setMonedaImportePercibido(String monedaImportePercibido) {
        this.monedaImportePercibido = monedaImportePercibido;
    }

    public Date getFechaPercepcion() {
        return fechaPercepcion;
    }

    public void setFechaPercepcion(Date fechaPercepcion) {
        this.fechaPercepcion = fechaPercepcion;
    }

    public Float getImporteTotalACobrar() {
        return importeTotalACobrar;
    }

    public void setImporteTotalACobrar(Float importeTotalACobrar) {
        this.importeTotalACobrar = importeTotalACobrar;
    }

    public String getMonedaImporteTotalACobrar() {
        return monedaImporteTotalACobrar;
    }

    public void setMonedaImporteTotalACobrar(String monedaImporteTotalACobrar) {
        this.monedaImporteTotalACobrar = monedaImporteTotalACobrar;
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
