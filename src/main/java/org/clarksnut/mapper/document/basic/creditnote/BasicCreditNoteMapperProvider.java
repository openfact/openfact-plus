package org.clarksnut.mapper.document.basic.creditnote;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
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
    public DocumentMapped map(XmlUBLFileModel file) {
        CreditNoteType creditNoteType = UBL21Reader.creditNote().read(file.getDocument());
        if (creditNoteType == null) {
            return null;
        }

        return new DocumentMapped() {
            @Override
            public DocumentBean getBean() {
                return new BasicCreditNoteBeanAdapter(creditNoteType);
            }

            @Override
            public Object getType() {
                return creditNoteType;
            }
        };
    }

}
