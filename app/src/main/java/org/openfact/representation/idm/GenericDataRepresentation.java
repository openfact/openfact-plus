package org.openfact.representation.idm;

public class GenericDataRepresentation {

    private Object data;

    public GenericDataRepresentation() {
    }

    public GenericDataRepresentation(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
