package org.openfact.datasource.peru;


import org.openfact.documents.reader.pe.common.jaxb.retention.RetentionType;
import org.openfact.files.*;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.utils.OpenfactModelUtils;
import org.openfact.datasource.DatasourceProvider;
import org.openfact.datasource.DatasourceType;
import org.openfact.datasource.peru.beans.RetentionBean;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@DatasourceType(datasource = "PeruRetentionDS")
public class PeruRetentionBeanProvider implements DatasourceProvider {

    @Override
    public Object getDatasource(XmlFileModel file) throws FileFetchException {
        RetentionType retentionType;
        try {
            retentionType = OpenfactModelUtils.unmarshall(file.getDocument(), RetentionType.class);
        } catch (JAXBException e) {
            return null;
        }

        RetentionBean bean = new RetentionBean();
        return bean;
    }

}
