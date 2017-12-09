package org.clarksnut.datasource.peru;

import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.utils.ClarksnutModelUtils;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@DatasourceType(datasource = "PeruCreditNoteDS")
public class PeruCreditNoteBeanProvider implements DatasourceProvider {

    @Override
    public boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException {
        return read(file) != null;
    }

    @Override
    public Datasource getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        CreditNoteType creditNoteType = read(file);
        if (creditNoteType == null) {
            return null;
        }

        CreditNoteDatasource bean = new CreditNoteDatasource();
        return bean;
    }

    private CreditNoteType read(XmlFileModel file) throws FileFetchException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), CreditNoteType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
