package org.openfact.documents.reader.pe.debitnote;

import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import org.jboss.logging.Logger;
import org.openfact.documents.DocumentReader;
import org.openfact.documents.GenericDocument;
import org.openfact.documents.jpa.entity.DocumentEntity;
import org.openfact.documents.jpa.entity.DocumentSpaceEntity;
import org.openfact.documents.reader.SupportedType;
import org.openfact.documents.reader.pe.common.PEUtils;
import org.openfact.files.XmlUBLFileModel;
import org.openfact.documents.InteractType;
import org.openfact.models.db.jpa.entity.SpaceEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Stateless
@SupportedType(value = "DebitNote")
public class PEDebitNoteReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(PEDebitNoteReader.class);

    @Inject
    private PEUtils peUtils;

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

        SpaceEntity senderSpaceEntity = peUtils.getSpace(debitNoteType.getAccountingSupplierParty());
        SpaceEntity receiverSpaceEntity = peUtils.getSpace(debitNoteType.getAccountingCustomerParty());

        DocumentEntity documentEntity = new DocumentEntity();

        DocumentSpaceEntity documentSpaceSenderEntity = new DocumentSpaceEntity();
        documentSpaceSenderEntity.setId(OpenfactModelUtils.generateId());
        documentSpaceSenderEntity.setType(InteractType.SENDER);
        documentSpaceSenderEntity.setSpace(senderSpaceEntity);
        documentSpaceSenderEntity.setDocument(documentEntity);

        DocumentSpaceEntity documentSpaceReceiverEntity = new DocumentSpaceEntity();
        documentSpaceReceiverEntity.setId(OpenfactModelUtils.generateId());
        documentSpaceReceiverEntity.setType(InteractType.RECEIVER);
        documentSpaceReceiverEntity.setSpace(receiverSpaceEntity);
        documentSpaceReceiverEntity.setDocument(documentEntity);

        documentEntity.setAssignedId(debitNoteType.getID().getValue());
        documentEntity.setSpaces(new HashSet<>(Arrays.asList(documentSpaceSenderEntity, documentSpaceReceiverEntity)));
        documentEntity.setSupplierAssignedId(debitNoteType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setSupplierName(debitNoteType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCustomerAssignedId(debitNoteType.getAccountingCustomerParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setCustomerName(debitNoteType.getAccountingCustomerParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCurrency(debitNoteType.getRequestedMonetaryTotal().getPayableAmount().getCurrencyID().value());
        documentEntity.setAmount(debitNoteType.getRequestedMonetaryTotal().getPayableAmount().getValue());
        documentEntity.setIssueDate(debitNoteType.getIssueDate().getValue().toGregorianCalendar().getTime());

        Map<String, String> tags = new HashMap<>();
        tags.put("reader", "peru");
        documentEntity.setTags(tags);

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
