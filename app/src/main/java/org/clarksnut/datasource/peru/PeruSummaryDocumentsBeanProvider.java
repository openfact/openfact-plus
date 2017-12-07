package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.datasource.peru.beans.SummaryDocumentsBean;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.utils.ClarksnutModelUtils;
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
            return ClarksnutModelUtils.unmarshall(file.getDocument(), SummaryDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
