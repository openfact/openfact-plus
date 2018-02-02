package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.peru.beans.ClienteBean;
import org.clarksnut.datasource.peru.beans.ProveedorBean;
import org.clarksnut.datasource.peru.beans.RetentionLineBean;

import java.util.Date;
import java.util.List;

public class PeruRetentionDatasource implements Datasource {

    private String idAsignado;
    private Date fechaEmision;

    private String regimen;
    private Float tasa;
    private String observaciones;

    private Float importeTotalRetenido;
    private String monedaImporteTotalRetenido;

    private Float importeTotalPagado;
    private String monedaImporteTotalPagado;

    private ProveedorBean emisor;
    private ClienteBean proveedor;

    private List<RetentionLineBean> detalle;

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

    public Float getImporteTotalRetenido() {
        return importeTotalRetenido;
    }

    public void setImporteTotalRetenido(Float importeTotalRetenido) {
        this.importeTotalRetenido = importeTotalRetenido;
    }

    public String getMonedaImporteTotalRetenido() {
        return monedaImporteTotalRetenido;
    }

    public void setMonedaImporteTotalRetenido(String monedaImporteTotalRetenido) {
        this.monedaImporteTotalRetenido = monedaImporteTotalRetenido;
    }

    public Float getImporteTotalPagado() {
        return importeTotalPagado;
    }

    public void setImporteTotalPagado(Float importeTotalPagado) {
        this.importeTotalPagado = importeTotalPagado;
    }

    public String getMonedaImporteTotalPagado() {
        return monedaImporteTotalPagado;
    }

    public void setMonedaImporteTotalPagado(String monedaImporteTotalPagado) {
        this.monedaImporteTotalPagado = monedaImporteTotalPagado;
    }

    public ProveedorBean getEmisor() {
        return emisor;
    }

    public void setEmisor(ProveedorBean emisor) {
        this.emisor = emisor;
    }

    public ClienteBean getProveedor() {
        return proveedor;
    }

    public void setProveedor(ClienteBean proveedor) {
        this.proveedor = proveedor;
    }

    public List<RetentionLineBean> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<RetentionLineBean> detalle) {
        this.detalle = detalle;
    }
}
