package org.openfact.documents.reader.pe.summarydocuments;

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
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Stateless
@SupportedType(value = "SummaryDocuments")
public class PESummaryDocumentsReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(PESummaryDocumentsReader.class);

    @Inject
    private PEUtils peUtils;

    @Override
    public String getSupportedDocumentType() {
        return "SummaryDocuments";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        SummaryDocumentsType summaryDocumentsType;
        try {
            summaryDocumentsType = OpenfactModelUtils.unmarshall(file.getDocument(), SummaryDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }

        SpaceEntity senderSpaceEntity = peUtils.getSpace(summaryDocumentsType.getAccountingSupplierParty());

        DocumentEntity documentEntity = new DocumentEntity();

        DocumentSpaceEntity documentSpaceSenderEntity = new DocumentSpaceEntity();
        documentSpaceSenderEntity.setId(OpenfactModelUtils.generateId());
        documentSpaceSenderEntity.setType(InteractType.SENDER);
        documentSpaceSenderEntity.setSpace(senderSpaceEntity);
        documentSpaceSenderEntity.setDocument(documentEntity);

        documentEntity.setAssignedId(summaryDocumentsType.getID().getValue());
        documentEntity.setSpaces(new HashSet<>(Arrays.asList(documentSpaceSenderEntity)));
        documentEntity.setSupplierAssignedId(summaryDocumentsType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setSupplierName(summaryDocumentsType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setIssueDate(summaryDocumentsType.getIssueDate().getValue().toGregorianCalendar().getTime());

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
                return summaryDocumentsType;
            }
        };
    }

}
