package org.openfact.datasource.peru;


import org.openfact.files.*;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.utils.OpenfactModelUtils;
import org.openfact.datasource.DatasourceProvider;
import org.openfact.datasource.DatasourceType;
import org.openfact.datasource.peru.beans.VoidedDocumentsBean;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@DatasourceType(region = "peru", documentType = "VoidedDocuments")
public class PeruVoidedDocumentsBeanProvider implements DatasourceProvider {

    @Override
    public Object getDatasource(XmlFileModel file) throws FileFetchException {
        VoidedDocumentsType voidedDocumentsType;
        try {
            voidedDocumentsType = OpenfactModelUtils.unmarshall(file.getDocument(), VoidedDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }

        VoidedDocumentsBean bean = new VoidedDocumentsBean();
        return bean;
    }

}
