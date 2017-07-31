package org.openfact.models.document;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.debitnote_21.DebitNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.openfact.models.DocumentReader;
import org.openfact.models.ModelException;
import org.w3c.dom.Document;

import javax.ejb.Stateless;
import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Stateless
public class DebitNoteReader implements DocumentReader {

    @Override
    public boolean canRead(String documentType) {
        return documentType.equals("DebitNote");
    }

    @Override
    public XContentBuilder read(Document document) {
        DebitNoteType debitNote = UBL21Reader.debitNote().read(document);
        if (debitNote != null) {
            try {
                return jsonBuilder()
                        .startObject()
                        .field("assignedId", debitNote.getIDValue())
                        .endObject();
            } catch (IOException e) {
                throw new ModelException("Internal Server Error, could not start Json Stream Object", e);
            }
        } else {
            throw new ModelException("Could not read document, please check document internal values");
        }
    }

}
