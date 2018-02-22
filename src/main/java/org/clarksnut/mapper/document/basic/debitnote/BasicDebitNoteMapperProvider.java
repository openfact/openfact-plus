package org.clarksnut.mapper.document.basic.debitnote;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.debitnote_21.DebitNoteType;
import org.clarksnut.documents.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapperProvider;
import org.jboss.logging.Logger;

public class BasicDebitNoteMapperProvider implements DocumentMapperProvider {

    private static final Logger logger = Logger.getLogger(BasicDebitNoteMapperProvider.class);

    @Override
    public String getGroup() {
        return "basic";
    }

    @Override
    public String getSupportedDocumentType() {
        return "DebitNote";
    }

    @Override
    public DocumentMapped map(XmlUBLFileModel file) throws ImpossibleToUnmarshallException {
        DebitNoteType debitNoteType = null;
        try {
            debitNoteType = UBL21Reader.debitNote().read(file.getDocument());
        } catch (Exception e) {
            // Nothing to do
        }
        if (debitNoteType == null) {
            throw new ImpossibleToUnmarshallException("Could not marshall to:" + DebitNoteType.class.getName());
        }

        final DebitNoteType type = debitNoteType;
        return new DocumentMapped() {
            @Override
            public DocumentBean getBean() {
                return new BasicDebitNoteBeanAdapter(type);
            }

            @Override
            public Object getType() {
                return type;
            }
        };
    }

}
