package org.clarksnut.datasource;

import org.clarksnut.models.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.files.XmlUBLFileModel;

public interface DatasourceProvider {

    String getName();

    boolean isInternal();

    Datasource getDatasource(XmlUBLFileModel file) throws ImpossibleToUnmarshallException;

}
