package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.peru.beans.ClienteBean;
import org.clarksnut.datasource.peru.beans.PerceptionLineBean;
import org.clarksnut.datasource.peru.beans.ProveedorBean;

import java.util.Date;
import java.util.List;

public class PeruPerceptionDatasource implements Datasource {

    private String idAsignado;
    private Date fechaEmision;

    private String regimen;
    private Float tasa;
    private String observaciones;

    private Float importeTotalPercibido;
    private String monedaImporteTotalPercibido;

    private Float importeTotalCobrado;
    private String monedaImporteTotalCobrado;

    private ProveedorBean emisor;
    private ClienteBean cliente;

    private List<PerceptionLineBean> detalle;

    public String getIdAsignado() {
        return idAsignado;
    }

    public void setIdAsignado(String idAsignado) {
        this.idAsignado = idAsignado;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public ProveedorBean getEmisor() {
        return emisor;
    }

    public void setEmisor(ProveedorBean emisor) {
        this.emisor = emisor;
    }

    public ClienteBean getCliente() {
        return cliente;
    }

    public void setCliente(ClienteBean cliente) {
        this.cliente = cliente;
    }

    public String getRegimen() {
        return regimen;
    }

    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    public Float getTasa() {
        return tasa;
    }

    public void setTasa(Float tasa) {
        this.tasa = tasa;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Float getImporteTotalPercibido() {
        return importeTotalPercibido;
    }

    public void setImporteTotalPercibido(Float importeTotalPercibido) {
        this.importeTotalPercibido = importeTotalPercibido;
    }

    public Float getImporteTotalCobrado() {
        return importeTotalCobrado;
    }

    public void setImporteTotalCobrado(Float importeTotalCobrado) {
        this.importeTotalCobrado = importeTotalCobrado;
    }

    public String getMonedaImporteTotalPercibido() {
        return monedaImporteTotalPercibido;
    }

    public void setMonedaImporteTotalPercibido(String monedaImporteTotalPercibido) {
        this.monedaImporteTotalPercibido = monedaImporteTotalPercibido;
    }

    public List<PerceptionLineBean> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<PerceptionLineBean> detalle) {
        this.detalle = detalle;
    }

    public String getMonedaImporteTotalCobrado() {
        return monedaImporteTotalCobrado;
    }

    public void setMonedaImporteTotalCobrado(String monedaImporteTotalCobrado) {
        this.monedaImporteTotalCobrado = monedaImporteTotalCobrado;
    }
}
