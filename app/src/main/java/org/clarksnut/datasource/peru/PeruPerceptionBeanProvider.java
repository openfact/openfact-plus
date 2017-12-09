package org.clarksnut.datasource.peru;


import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.reader.pe.common.jaxb.perception.PerceptionType;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.utils.ClarksnutModelUtils;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@DatasourceType(datasource = "PeruPerceptionDS")
public class PeruPerceptionBeanProvider implements DatasourceProvider {

    @Override
    public boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException {
        return read(file) != null;
    }

    @Override
    public Datasource getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        PerceptionType perceptionType = read(file);
        if (perceptionType == null) {
            return null;
        }

        PerceptionDatasource bean = new PerceptionDatasource();
        return bean;
    }

    private PerceptionType read(XmlFileModel file) throws FileFetchException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), PerceptionType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
