package org.openfact.representations.idm;

import java.util.Map;

public class GenericDataRepresentation {

    private Object data;
    private Map<String, String> links;
    private Map<String, Object> meta;

    public GenericDataRepresentation() {
    }

    public GenericDataRepresentation(Object data) {
        this.data = data;
    }

    public GenericDataRepresentation(Object data, Map<String, String> links) {
        this.data = data;
        this.links = links;
    }

    public GenericDataRepresentation(Object data, Map<String, String> links, Map<String, Object> meta) {
        this.data = data;
        this.links = links;
        this.meta = meta;
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

    public Map<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }
}
