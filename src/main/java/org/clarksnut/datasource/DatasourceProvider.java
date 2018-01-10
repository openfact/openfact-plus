package org.clarksnut.datasource;

import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.files.exceptions.FileFetchException;

public interface DatasourceProvider {

    String getName();

    boolean isInternal();

    Datasource getDatasource(XmlUBLFileModel file) throws FileFetchException;

}
