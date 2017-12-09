package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@DatasourceType(datasource = "PeruVoidedDocumentsDS")
public class PeruVoidedDocumentsBeanProvider implements DatasourceProvider {

    @Override
    public boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException {
        return read(file) != null;
    }

    @Override
    public Datasource getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        VoidedDocumentsType voidedDocumentsType = read(file);
        if (file == null) {
            return null;
        }

        VoidedDocumentsDatasource bean = new VoidedDocumentsDatasource();
        return bean;
    }

    private VoidedDocumentsType read(XmlFileModel file) throws FileFetchException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), VoidedDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
