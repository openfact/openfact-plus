package org.openfact.documents.reader.pe.debitnote;

import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import org.jboss.logging.Logger;
import org.openfact.documents.DocumentReader;
import org.openfact.documents.GenericDocument;
import org.openfact.documents.jpa.entity.DocumentEntity;
import org.openfact.documents.reader.SupportedType;
import org.openfact.files.XmlUBLFileModel;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;

@Stateless
@SupportedType(value = "DebitNote")
public class PEDebitNoteReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(PEDebitNoteReader.class);

    @Override
    public String getSupportedDocumentType() {
        return "DebitNote";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        DebitNoteType debitNoteType;
        try {
            debitNoteType = OpenfactModelUtils.unmarshall(file.getDocument(), DebitNoteType.class);
        } catch (JAXBException e) {
            return null;
        }

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setAssignedId(debitNoteType.getID().getValue());
        documentEntity.setSupplierAssignedId(debitNoteType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setSupplierName(debitNoteType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCustomerAssignedId(debitNoteType.getAccountingCustomerParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setCustomerName(debitNoteType.getAccountingCustomerParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCurrency(debitNoteType.getRequestedMonetaryTotal().getPayableAmount().getCurrencyID().value());
        documentEntity.setAmount(debitNoteType.getRequestedMonetaryTotal().getPayableAmount().getValue().floatValue());
        documentEntity.setIssueDate(debitNoteType.getIssueDate().getValue().toGregorianCalendar().getTime());

        return new GenericDocument() {
            @Override
            public DocumentEntity getEntity() {
                return documentEntity;
            }

            @Override
            public Object getJaxb() {
                return debitNoteType;
            }
        };
    }

}
