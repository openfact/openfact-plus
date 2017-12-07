package org.clarksnut.documents.reader.pe.perception;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import org.clarksnut.documents.DocumentReader;
import org.clarksnut.documents.GenericDocument;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.reader.SupportedType;
import org.clarksnut.documents.reader.pe.common.jaxb.perception.PerceptionType;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

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
            perceptionType = ClarksnutModelUtils.unmarshall(file.getDocument(), PerceptionType.class);
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

        // Postal address
        AddressType supplierPostalAddressType = perceptionType.getAgentParty().getPostalAddress();
        documentEntity.setSupplierStreetAddress(supplierPostalAddressType.getStreetName().getValue());
        documentEntity.setSupplierCity(supplierPostalAddressType.getCitySubdivisionName().getValue() + ", " + supplierPostalAddressType.getCityName().getValue() + ", " + supplierPostalAddressType.getCitySubdivisionName().getValue());
        documentEntity.setSupplierCountry(supplierPostalAddressType.getCountry().getIdentificationCode().getValue());

        AddressType customerPostalAddressType = perceptionType.getReceiverParty().getPostalAddress();
        if (customerPostalAddressType != null) {
            documentEntity.setCustomerStreetAddress(customerPostalAddressType.getStreetName().getValue());
            documentEntity.setCustomerCity(customerPostalAddressType.getCitySubdivisionName().getValue() + ", " + customerPostalAddressType.getCityName().getValue() + ", " + customerPostalAddressType.getCitySubdivisionName().getValue());
            documentEntity.setCustomerCountry(customerPostalAddressType.getCountry().getIdentificationCode().getValue());
        }

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
