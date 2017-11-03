package org.openfact.datasource.peru;

import org.openfact.datasource.DatasourceProvider;
import org.openfact.datasource.DatasourceType;
import org.openfact.datasource.peru.beans.SummaryDocumentsBean;
import org.openfact.documents.DocumentModel;
import org.openfact.files.XmlFileModel;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.utils.OpenfactModelUtils;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@DatasourceType(datasource = "PeruSummaryDocumentsDS")
public class PeruSummaryDocumentsBeanProvider implements DatasourceProvider {

    @Override
    public boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException {
        return read(file) != null;
    }

    @Override
    public Object getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        SummaryDocumentsType summaryDocumentsType = read(file);
        if (summaryDocumentsType == null) {
            return null;
        }

        SummaryDocumentsBean bean = new SummaryDocumentsBean();
        return bean;
    }

    private SummaryDocumentsType read(XmlFileModel file) throws FileFetchException {
        try {
            return OpenfactModelUtils.unmarshall(file.getDocument(), SummaryDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
