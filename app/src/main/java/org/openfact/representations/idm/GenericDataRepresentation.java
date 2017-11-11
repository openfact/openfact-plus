package org.openfact.representations.idm;

import java.util.Map;

public class GenericDataRepresentation<T> {

    private T data;
    private Map<String, String> links;
    private Map<String, Object> meta;

    public GenericDataRepresentation() {
    }

    public GenericDataRepresentation(T data) {
        this.data = data;
    }

    public GenericDataRepresentation(T data, Map<String, String> links) {
        this.data = data;
        this.links = links;
    }

    public GenericDataRepresentation(T data, Map<String, String> links, Map<String, Object> meta) {
        this.data = data;
        this.links = links;
        this.meta = meta;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
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
