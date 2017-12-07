package org.clarksnut.datasource.peru;


import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.datasource.peru.beans.DebitNoteBean;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@DatasourceType(datasource = "PeruDebitNoteDS")
public class PeruDebitNoteBeanProvider implements DatasourceProvider {

    @Override
    public boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException {
        return read(file) != null;
    }

    @Override
    public Object getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        DebitNoteType debitNoteType = read(file);
        if (debitNoteType == null) {
            return null;
        }

        DebitNoteBean bean = new DebitNoteBean();
        return bean;
    }

    private DebitNoteType read(XmlFileModel file) throws FileFetchException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), DebitNoteType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}