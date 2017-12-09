package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.peru.beans.ClienteBean;
import org.clarksnut.datasource.peru.beans.LineBean;
import org.clarksnut.datasource.peru.beans.ProveedorBean;

import java.util.Date;
import java.util.List;

public class CreditNoteDatasource extends Datasource {

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

    private Float totalGravada;
    private Float totalGratuita;
    private Float totalExonerada;
    private Float totalInafecta;

    private Float totalVenta;
    private Float totalDescuentoGlobal;
    private Float totalOtrosCargos;

    private Float totalIgv;
    private Float totalIsc;
    private Float totalOtrosTributos;

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

    public Float getTotalGravada() {
        return totalGravada;
    }

    public void setTotalGravada(Float totalGravada) {
        this.totalGravada = totalGravada;
    }

    public Float getTotalGratuita() {
        return totalGratuita;
    }

    public void setTotalGratuita(Float totalGratuita) {
        this.totalGratuita = totalGratuita;
    }

    public Float getTotalExonerada() {
        return totalExonerada;
    }

    public void setTotalExonerada(Float totalExonerada) {
        this.totalExonerada = totalExonerada;
    }

    public Float getTotalInafecta() {
        return totalInafecta;
    }

    public void setTotalInafecta(Float totalInafecta) {
        this.totalInafecta = totalInafecta;
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

    public List<LineBean> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<LineBean> detalle) {
        this.detalle = detalle;
    }
}
