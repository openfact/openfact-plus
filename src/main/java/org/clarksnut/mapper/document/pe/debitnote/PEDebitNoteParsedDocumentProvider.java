package org.clarksnut.mapper.document.pe.debitnote;

import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapperProvider;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;

import javax.xml.bind.JAXBException;

public class PEDebitNoteParsedDocumentProvider implements DocumentMapperProvider {

    private static final Logger logger = Logger.getLogger(PEDebitNoteParsedDocumentProvider.class);

    @Override
    public String getGroup() {
        return "peru";
    }

    @Override
    public String getSupportedDocumentType() {
        return "DebitNote";
    }

    @Override
    public DocumentMapped map(XmlUBLFileModel file) {
        DebitNoteType debitNoteType;
        try {
            debitNoteType = ClarksnutModelUtils.unmarshall(file.getDocument(), DebitNoteType.class);
        } catch (JAXBException e) {
            return null;
        }

        return new DocumentMapped() {
            @Override
            public DocumentBean getBean() {
                return new PEDebitNoteBeanAdapter(debitNoteType);
            }

            @Override
            public Object getType() {
                return debitNoteType;
            }
        };
    }

}
