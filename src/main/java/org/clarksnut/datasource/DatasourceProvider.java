package org.clarksnut.datasource;

import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.exceptions.ImpossibleToUnmarshallException;

public interface DatasourceProvider {

    String getName();

    boolean isInternal();

    Datasource getDatasource(XmlUBLFileModel file) throws ImpossibleToUnmarshallException;

}
