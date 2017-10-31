package org.openfact.documents.reader.pe.perception;

import org.jboss.logging.Logger;
import org.openfact.documents.DocumentReader;
import org.openfact.documents.GenericDocument;
import org.openfact.documents.jpa.entity.DocumentEntity;
import org.openfact.documents.reader.SupportedType;
import org.openfact.documents.reader.pe.common.jaxb.perception.PerceptionType;
import org.openfact.files.XmlUBLFileModel;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;

@Stateless
@SupportedType(value = "Perception")
public class PEPerceptionReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(PEPerceptionReader.class);

    @Override
    public String getSupportedDocumentType() {
        return "Perception";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        PerceptionType perceptionType;
        try {
            perceptionType = OpenfactModelUtils.unmarshall(file.getDocument(), PerceptionType.class);
        } catch (JAXBException e) {
            return null;
        }

        DocumentEntity documentEntity = new DocumentEntity();

        documentEntity.setAssignedId(perceptionType.getId().getValue());
        documentEntity.setSupplierAssignedId(perceptionType.getAgentParty().getPartyIdentification().get(0).getIDValue());
        documentEntity.setSupplierName(perceptionType.getAgentParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCustomerAssignedId(perceptionType.getReceiverParty().getPartyIdentification().get(0).getIDValue());
        documentEntity.setCustomerName(perceptionType.getReceiverParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCurrency(perceptionType.getSunatTotalCashed().getCurrencyID());
        documentEntity.setAmount(perceptionType.getSunatTotalCashed().getValue().floatValue());
        documentEntity.setIssueDate(perceptionType.getIssueDate().getValue().toGregorianCalendar().getTime());

        return new GenericDocument() {
            @Override
            public DocumentEntity getEntity() {
                return documentEntity;
            }

            @Override
            public Object getJaxb() {
                return perceptionType;
            }
        };
    }

}
