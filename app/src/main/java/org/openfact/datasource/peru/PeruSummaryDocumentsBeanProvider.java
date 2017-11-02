package org.openfact.datasource.peru;


import org.openfact.files.*;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.utils.OpenfactModelUtils;
import org.openfact.datasource.DatasourceProvider;
import org.openfact.datasource.DatasourceType;
import org.openfact.datasource.peru.beans.SummaryDocumentsBean;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@DatasourceType(region = "peru", documentType = "SummaryDocuments")
public class PeruSummaryDocumentsBeanProvider implements DatasourceProvider {

    @Override
    public Object getDatasource(XmlFileModel file) throws FileFetchException {
        SummaryDocumentsType summaryDocumentsType;
        try {
            summaryDocumentsType = OpenfactModelUtils.unmarshall(file.getDocument(), SummaryDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }

        SummaryDocumentsBean bean = new SummaryDocumentsBean();
        return bean;
    }

}
