package org.clarksnut.datasource.peru.beans;

public class LineBean {

    private Float cantidad;
    private String unidadMedida;
    private String codidoProducto;
    private String descripcion;

    private Float precioUnitario; // Sin impuestos y sin cantidad
    private Float precioVentaUnitario; // Con impuestos y sin cantidad
    private Float valorReferencialUnitarioEnOperacionesNoOnerosas;
    private Float totalValorVenta; // con cantidad
    private Float totalDescuento;

    private Float totalIgv;
    private Float totalIsc;

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
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

    public Float getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Float precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Float getTotalValorVenta() {
        return totalValorVenta;
    }

    public void setTotalValorVenta(Float totalValorVenta) {
        this.totalValorVenta = totalValorVenta;
    }

    public Float getValorReferencialUnitarioEnOperacionesNoOnerosas() {
        return valorReferencialUnitarioEnOperacionesNoOnerosas;
    }

    public void setValorReferencialUnitarioEnOperacionesNoOnerosas(Float valorReferencialUnitarioEnOperacionesNoOnerosas) {
        this.valorReferencialUnitarioEnOperacionesNoOnerosas = valorReferencialUnitarioEnOperacionesNoOnerosas;
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

    public Float getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(Float totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    public Float getPrecioVentaUnitario() {
        return precioVentaUnitario;
    }

    public void setPrecioVentaUnitario(Float precioVentaUnitario) {
        this.precioVentaUnitario = precioVentaUnitario;
    }
}
