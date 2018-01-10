package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.peru.beans.ProveedorBean;
import org.clarksnut.datasource.peru.beans.SummaryLineBean;

import java.util.Date;
import java.util.List;

public class PeruSummaryDocumentsDatasource extends Datasource {

    private String idAsignado;

    private Date fechaEmision;
    private Date fechaGeneracion;

    private ProveedorBean proveedor;

    private List<SummaryLineBean> detalle;

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

    public Date getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(Date fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public ProveedorBean getProveedor() {
        return proveedor;
    }

    public void setProveedor(ProveedorBean proveedor) {
        this.proveedor = proveedor;
    }

    public List<SummaryLineBean> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<SummaryLineBean> detalle) {
        this.detalle = detalle;
    }
}
