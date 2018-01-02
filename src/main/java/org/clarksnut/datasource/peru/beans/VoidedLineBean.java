package org.clarksnut.datasource.peru.beans;

public class VoidedLineBean {

    private String tipoDocumento;
    private String documentoSerie;
    private String documentoNumero;
    private String motivoBaja;

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumentoSerie() {
        return documentoSerie;
    }

    public void setDocumentoSerie(String documentoSerie) {
        this.documentoSerie = documentoSerie;
    }

    public String getDocumentoNumero() {
        return documentoNumero;
    }

    public void setDocumentoNumero(String documentoNumero) {
        this.documentoNumero = documentoNumero;
    }

    public String getMotivoBaja() {
        return motivoBaja;
    }

    public void setMotivoBaja(String motivoBaja) {
        this.motivoBaja = motivoBaja;
    }
}
