package org.openfact.datasource;

import org.openfact.documents.DocumentModel;
import org.openfact.files.XmlFileModel;
import org.openfact.files.exceptions.FileFetchException;

public interface DatasourceProvider {

    boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException;

    Object getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException;

}
