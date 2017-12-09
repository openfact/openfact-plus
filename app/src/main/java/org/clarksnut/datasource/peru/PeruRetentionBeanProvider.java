package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.reader.pe.common.jaxb.retention.RetentionType;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.utils.ClarksnutModelUtils;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@DatasourceType(datasource = "PeruRetentionDS")
public class PeruRetentionBeanProvider implements DatasourceProvider {

    @Override
    public boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException {
        return read(file) != null;
    }

    @Override
    public Datasource getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        RetentionType retentionType = read(file);
        if (retentionType == null) {
            return null;
        }

        RetentionDatasource bean = new RetentionDatasource();
        return bean;
    }

    private RetentionType read(XmlFileModel file) throws FileFetchException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), RetentionType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
