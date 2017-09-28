package org.openfact.models;

public interface XmlUblFileModel extends XmlFileModel {

    boolean isUblFile(XmlFileModel file) throws ModelFetchException;

}
