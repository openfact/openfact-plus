package org.clarksnut.datasource.peru.beans;

public class ProveedorBean {

    private String nombre;
    private String idAssignado;
    private String tipoDocumento;
    private PostalAddressBean direccion;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdAssignado() {
        return idAssignado;
    }

    public void setIdAssignado(String idAssignado) {
        this.idAssignado = idAssignado;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public PostalAddressBean getDireccion() {
        return direccion;
    }

    public void setDireccion(PostalAddressBean direccion) {
        this.direccion = direccion;
    }

}
