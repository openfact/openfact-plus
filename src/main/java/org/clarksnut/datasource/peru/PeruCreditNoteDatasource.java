package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.peru.beans.*;

import java.util.Date;
import java.util.List;

public class PeruCreditNoteDatasource implements Datasource {

    private String idAsignado;
    private String moneda;
    private Date fechaEmision;

    private String tipoNotaCredito;
    private String documentoModifica;
    private String tipoDocumentoModifica;

    private ProveedorBean proveedor;
    private ClienteBean cliente;

    private String numeroGuiaRemisionRelacionada;
    private String otroDocumentoRelacionadoId;

    private String motivoSustento;

    private InformacionAdicionalBean informacionAdicional;

    private Float totalVenta;
    private Float totalDescuentoGlobal;
    private Float totalOtrosCargos;

    private TributosBean tributos;

    private List<LineBean> detalle;

    public String getIdAsignado() {
        return idAsignado;
    }

    public void setIdAsignado(String idAsignado) {
        this.idAsignado = idAsignado;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getTipoNotaCredito() {
        return tipoNotaCredito;
    }

    public void setTipoNotaCredito(String tipoNotaCredito) {
        this.tipoNotaCredito = tipoNotaCredito;
    }

    public String getDocumentoModifica() {
        return documentoModifica;
    }

    public void setDocumentoModifica(String documentoModifica) {
        this.documentoModifica = documentoModifica;
    }

    public String getTipoDocumentoModifica() {
        return tipoDocumentoModifica;
    }

    public void setTipoDocumentoModifica(String tipoDocumentoModifica) {
        this.tipoDocumentoModifica = tipoDocumentoModifica;
    }

    public ProveedorBean getProveedor() {
        return proveedor;
    }

    public void setProveedor(ProveedorBean proveedor) {
        this.proveedor = proveedor;
    }

    public ClienteBean getCliente() {
        return cliente;
    }

    public void setCliente(ClienteBean cliente) {
        this.cliente = cliente;
    }

    public String getNumeroGuiaRemisionRelacionada() {
        return numeroGuiaRemisionRelacionada;
    }

    public void setNumeroGuiaRemisionRelacionada(String numeroGuiaRemisionRelacionada) {
        this.numeroGuiaRemisionRelacionada = numeroGuiaRemisionRelacionada;
    }

    public String getOtroDocumentoRelacionadoId() {
        return otroDocumentoRelacionadoId;
    }

    public void setOtroDocumentoRelacionadoId(String otroDocumentoRelacionadoId) {
        this.otroDocumentoRelacionadoId = otroDocumentoRelacionadoId;
    }

    public String getMotivoSustento() {
        return motivoSustento;
    }

    public void setMotivoSustento(String motivoSustento) {
        this.motivoSustento = motivoSustento;
    }

    public Float getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(Float totalVenta) {
        this.totalVenta = totalVenta;
    }

    public Float getTotalDescuentoGlobal() {
        return totalDescuentoGlobal;
    }

    public void setTotalDescuentoGlobal(Float totalDescuentoGlobal) {
        this.totalDescuentoGlobal = totalDescuentoGlobal;
    }

    public Float getTotalOtrosCargos() {
        return totalOtrosCargos;
    }

    public void setTotalOtrosCargos(Float totalOtrosCargos) {
        this.totalOtrosCargos = totalOtrosCargos;
    }

    public List<LineBean> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<LineBean> detalle) {
        this.detalle = detalle;
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
}
