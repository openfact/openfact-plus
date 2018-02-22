package org.clarksnut.datasource;

import org.clarksnut.documents.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.files.XmlUBLFileModel;

public interface DatasourceProvider {

    String getName();

    boolean isInternal();

    Datasource getDatasource(XmlUBLFileModel file) throws ImpossibleToUnmarshallException;

}
