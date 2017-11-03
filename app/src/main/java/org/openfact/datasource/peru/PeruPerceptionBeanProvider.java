package org.openfact.datasource.peru;


import org.openfact.documents.reader.pe.common.jaxb.perception.PerceptionType;
import org.openfact.files.*;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.utils.OpenfactModelUtils;
import org.openfact.datasource.DatasourceProvider;
import org.openfact.datasource.DatasourceType;
import org.openfact.datasource.peru.beans.PerceptionBean;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@DatasourceType(datasource = "PeruPerceptionDS")
public class PeruPerceptionBeanProvider implements DatasourceProvider {

    @Override
    public Object getDatasource(XmlFileModel file) throws FileFetchException {
        PerceptionType perceptionType;
        try {
            perceptionType = OpenfactModelUtils.unmarshall(file.getDocument(), PerceptionType.class);
        } catch (JAXBException e) {
            return null;
        }

        PerceptionBean bean = new PerceptionBean();
        return bean;
    }

}
