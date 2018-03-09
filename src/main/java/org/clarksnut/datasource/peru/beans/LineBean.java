package org.clarksnut.datasource.peru.beans;

public class LineBean {

    private Double cantidad;
    private String unidadMedida;
    private String codidoProducto;
    private String descripcion;

    private Double precioUnitario; // Sin impuestos y sin cantidad
    private Double precioVentaUnitario; // Con impuestos y sin cantidad
    private Double valorReferencialUnitarioEnOperacionesNoOnerosas;
    private Double totalValorVenta; // con cantidad
    private Double totalDescuento;

    private Double totalIgv;
    private Double totalIsc;

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getCodidoProducto() {
        return codidoProducto;
    }

    public void setCodidoProducto(String codidoProducto) {
        this.codidoProducto = codidoProducto;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Double getTotalValorVenta() {
        return totalValorVenta;
    }

    public void setTotalValorVenta(Double totalValorVenta) {
        this.totalValorVenta = totalValorVenta;
    }

    public Double getValorReferencialUnitarioEnOperacionesNoOnerosas() {
        return valorReferencialUnitarioEnOperacionesNoOnerosas;
    }

    public void setValorReferencialUnitarioEnOperacionesNoOnerosas(Double valorReferencialUnitarioEnOperacionesNoOnerosas) {
        this.valorReferencialUnitarioEnOperacionesNoOnerosas = valorReferencialUnitarioEnOperacionesNoOnerosas;
    }

    public Double getTotalIgv() {
        return totalIgv;
    }

    public void setTotalIgv(Double totalIgv) {
        this.totalIgv = totalIgv;
    }

    public Double getTotalIsc() {
        return totalIsc;
    }

    public void setTotalIsc(Double totalIsc) {
        this.totalIsc = totalIsc;
    }

    public Double getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(Double totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    public Double getPrecioVentaUnitario() {
        return precioVentaUnitario;
    }

    public void setPrecioVentaUnitario(Double precioVentaUnitario) {
        this.precioVentaUnitario = precioVentaUnitario;
    }
}
