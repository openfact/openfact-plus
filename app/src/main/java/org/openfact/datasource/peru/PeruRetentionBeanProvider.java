package org.openfact.datasource.peru;

import org.openfact.datasource.DatasourceProvider;
import org.openfact.datasource.DatasourceType;
import org.openfact.datasource.peru.beans.RetentionBean;
import org.openfact.documents.DocumentModel;
import org.openfact.documents.reader.pe.common.jaxb.retention.RetentionType;
import org.openfact.files.XmlFileModel;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.utils.OpenfactModelUtils;

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
    public Object getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        RetentionType retentionType = read(file);
        if (retentionType == null) {
            return null;
        }

        RetentionBean bean = new RetentionBean();
        return bean;
    }

    private RetentionType read(XmlFileModel file) throws FileFetchException {
        try {
            return OpenfactModelUtils.unmarshall(file.getDocument(), RetentionType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
