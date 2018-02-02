package org.clarksnut.datasource.basic;

import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.files.XmlUBLFileModel;

public class EmptyDatasourceProvider implements DatasourceProvider {

    @Override
    public String getName() {
        return "EmptyDS";
    }

    @Override
    public boolean isInternal() {
        return true;
    }

    @Override
    public Datasource getDatasource(XmlUBLFileModel file) {
        return new EmptyDatasource();
    }

}
