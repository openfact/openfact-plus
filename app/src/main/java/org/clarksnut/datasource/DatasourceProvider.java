package org.clarksnut.datasource;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;

public interface DatasourceProvider {

    boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException;

    Datasource getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException;

}
