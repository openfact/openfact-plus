package org.openfact.datasource.peru;

import org.openfact.datasource.DatasourceProvider;
import org.openfact.datasource.DatasourceType;
import org.openfact.datasource.peru.beans.VoidedDocumentsBean;
import org.openfact.documents.DocumentModel;
import org.openfact.files.XmlFileModel;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.utils.OpenfactModelUtils;
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
    public Object getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        VoidedDocumentsType voidedDocumentsType = read(file);
        if (file == null) {
            return null;
        }

        VoidedDocumentsBean bean = new VoidedDocumentsBean();
        return bean;
    }

    private VoidedDocumentsType read(XmlFileModel file) throws FileFetchException {
        try {
            return OpenfactModelUtils.unmarshall(file.getDocument(), VoidedDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
