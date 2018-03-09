package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.peru.beans.*;

import java.util.Date;
import java.util.List;

public class PeruInvoiceDatasource implements Datasource {

    private String idAsignado;
    private String tipoDocumento;
    private String moneda;

    private Date fechaEmision;
    private Date fechaVencimiento;

    private ProveedorBean proveedor;
    private ClienteBean cliente;

    private PostalAddressBean direccionEnvio;

    private String numeroGuiaRemisionRelacionada;
    private String otroDocumentoRelacionadoId;

    private InformacionAdicionalBean informacionAdicional;

    private Double totalVenta;
    private Double totalDescuentoGlobal;
    private Double totalOtrosCargos;

    private TributosBean tributos;

    private List<LineBean> detalle;

    public String getIdAsignado() {
        return idAsignado;
    }

    public void setIdAsignado(String idAsignado) {
        this.idAsignado = idAsignado;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
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

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
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

    public PostalAddressBean getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(PostalAddressBean direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
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

    public Double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(Double totalVenta) {
        this.totalVenta = totalVenta;
    }

    public Double getTotalDescuentoGlobal() {
        return totalDescuentoGlobal;
    }

    public void setTotalDescuentoGlobal(Double totalDescuentoGlobal) {
        this.totalDescuentoGlobal = totalDescuentoGlobal;
    }

    public Double getTotalOtrosCargos() {
        return totalOtrosCargos;
    }

    public void setTotalOtrosCargos(Double totalOtrosCargos) {
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
