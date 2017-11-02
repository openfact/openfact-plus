package org.openfact.datasource;

import org.openfact.files.XmlFileModel;
import org.openfact.files.exceptions.FileFetchException;

public interface DatasourceProvider {

    Object getDatasource(XmlFileModel file) throws FileFetchException;

}
