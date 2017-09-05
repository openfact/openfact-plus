package org.openfact.representation.idm;

import java.util.Map;

public class GenericDataRepresentation {

    private Object data;
    private Map<String, String> links;

    public GenericDataRepresentation() {
    }

    public GenericDataRepresentation(Object data) {
        this.data = data;
    }

    public GenericDataRepresentation(Object data, Map<String, String> links) {
        this.data = data;
        this.links = links;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
