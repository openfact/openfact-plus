package org.clarksnut.mapper.document.basic.creditnote;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import org.clarksnut.documents.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapperProvider;
import org.jboss.logging.Logger;

public class BasicCreditNoteMapperProvider implements DocumentMapperProvider {

    private static final Logger logger = Logger.getLogger(BasicCreditNoteMapperProvider.class);

    @Override
    public String getGroup() {
        return "basic";
    }

    @Override
    public String getSupportedDocumentType() {
        return "CreditNote";
    }

    @Override
    public DocumentMapped map(XmlUBLFileModel file) throws ImpossibleToUnmarshallException {
        CreditNoteType creditNoteType = null;
        try {
            creditNoteType = UBL21Reader.creditNote().read(file.getDocument());
        } catch (Exception e) {
            // Nothing to do
        }
        if (creditNoteType == null) {
            throw new ImpossibleToUnmarshallException("Could not marshall to:" + CreditNoteType.class.getName());
        }

        final CreditNoteType type = creditNoteType;
        return new DocumentMapped() {
            @Override
            public DocumentBean getBean() {
                return new BasicCreditNoteBeanAdapter(type);
            }

            @Override
            public Object getType() {
                return type;
            }
        };
    }

}
