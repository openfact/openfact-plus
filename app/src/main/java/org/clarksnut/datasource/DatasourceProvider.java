package org.clarksnut.datasource;

import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;

public interface DatasourceProvider {

    boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException;

    Object getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException;

}
