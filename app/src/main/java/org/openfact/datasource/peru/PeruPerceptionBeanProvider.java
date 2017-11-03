package org.openfact.datasource.peru;


import org.openfact.datasource.DatasourceProvider;
import org.openfact.datasource.DatasourceType;
import org.openfact.datasource.peru.beans.PerceptionBean;
import org.openfact.documents.DocumentModel;
import org.openfact.documents.reader.pe.common.jaxb.perception.PerceptionType;
import org.openfact.files.XmlFileModel;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.utils.OpenfactModelUtils;

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
    public Object getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        PerceptionType perceptionType = read(file);
        if (perceptionType == null) {
            return null;
        }

        PerceptionBean bean = new PerceptionBean();
        return bean;
    }

    private PerceptionType read(XmlFileModel file) throws FileFetchException {
        try {
            return OpenfactModelUtils.unmarshall(file.getDocument(), PerceptionType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
