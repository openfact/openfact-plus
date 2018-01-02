package org.clarksnut.batchs.support.io;

public class MixedObject {

    private final Object object;
    private final String operation;

    public MixedObject(Object obj) {
        this.object = obj;
        this.operation = "persist";
    }

    public MixedObject(Object obj, String operation) {
        this.object = obj;
        this.operation = operation;
    }

    public Object getObject() {
        return object;
    }

    public String getOperation() {
        return operation;
    }

}
