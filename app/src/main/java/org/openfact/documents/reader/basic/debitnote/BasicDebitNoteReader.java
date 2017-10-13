package org.openfact.documents.reader.basic.debitnote;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.debitnote_21.DebitNoteType;
import org.jboss.logging.Logger;
import org.openfact.documents.DocumentReader;
import org.openfact.documents.GenericDocument;
import org.openfact.documents.jpa.entity.DocumentEntity;
import org.openfact.documents.jpa.entity.DocumentSpaceEntity;
import org.openfact.documents.reader.SupportedType;
import org.openfact.documents.reader.basic.common.BasicUtils;
import org.openfact.files.XmlUBLFileModel;
import org.openfact.models.InteractType;
import org.openfact.models.db.jpa.entity.SpaceEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;

@Stateless
@SupportedType(value = "DebitNote")
public class BasicDebitNoteReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(BasicDebitNoteReader.class);

    @Inject
    private BasicUtils basicUtils;

    @Override
    public String getSupportedDocumentType() {
        return "DebitNote";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        DebitNoteType debitNoteType = UBL21Reader.debitNote().read(file.getDocument());
        if (debitNoteType == null) {
            return null;
        }

        SpaceEntity senderSpaceEntity = basicUtils.getSenderSpace(debitNoteType.getAccountingSupplierParty());
        SpaceEntity receiverSpaceEntity = basicUtils.getReceiverSpace(debitNoteType.getAccountingCustomerParty());

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

        documentEntity.setFileId(file.getId());
        documentEntity.setAssignedId(debitNoteType.getIDValue());
        documentEntity.setSpaces(new HashSet<>(Arrays.asList(documentSpaceSenderEntity, documentSpaceReceiverEntity)));

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
