package org.openfact.models;

public interface XmlFileModel extends FileModel {

    boolean isXmlFile(FileModel file) throws ModelFetchException;

}
