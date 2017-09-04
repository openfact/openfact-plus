package org.openfact.representation.idm;

public class DataRepresentation {

    private Object data;

    public DataRepresentation() {

    }

    public DataRepresentation(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
