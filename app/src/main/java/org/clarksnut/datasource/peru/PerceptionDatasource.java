package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.peru.beans.*;

import java.util.Date;
import java.util.List;

public class PerceptionDatasource extends Datasource {

    private String idAsignado;
    private Date fechaEmision;

    private ProveedorBean emisor;
    private ClienteBean cliente;

    private String regimenPercepcion;
    private Float tasaPercepcion;

    private String observaciones;

    private String moneda;
    private Float totalPercibido;
    private Float totalCobrado;

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

    public String getRegimenPercepcion() {
        return regimenPercepcion;
    }

    public void setRegimenPercepcion(String regimenPercepcion) {
        this.regimenPercepcion = regimenPercepcion;
    }

    public Float getTasaPercepcion() {
        return tasaPercepcion;
    }

    public void setTasaPercepcion(Float tasaPercepcion) {
        this.tasaPercepcion = tasaPercepcion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Float getTotalPercibido() {
        return totalPercibido;
    }

    public void setTotalPercibido(Float totalPercibido) {
        this.totalPercibido = totalPercibido;
    }

    public Float getTotalCobrado() {
        return totalCobrado;
    }

    public void setTotalCobrado(Float totalCobrado) {
        this.totalCobrado = totalCobrado;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public List<PerceptionLineBean> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<PerceptionLineBean> detalle) {
        this.detalle = detalle;
    }
}
