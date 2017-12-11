package org.clarksnut.documents.parser.pe.perception;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import org.clarksnut.documents.parser.ParsedDocument;
import org.clarksnut.documents.parser.ParsedDocumentProvider;
import org.clarksnut.documents.parser.SkeletonDocument;
import org.clarksnut.documents.parser.SupportedDocumentType;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;
import org.openfact.perception.PerceptionType;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@SupportedDocumentType(value = "Perception")
public class PEPerceptionReader implements ParsedDocumentProvider {

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
    public ParsedDocument read(XmlUBLFileModel file) {
        PerceptionType perceptionType;
        try {
            perceptionType = ClarksnutModelUtils.unmarshall(file.getDocument(), PerceptionType.class);
        } catch (JAXBException e) {
            return null;
        }

        SkeletonDocument skeleton = new SkeletonDocument();
        skeleton.setType(getSupportedDocumentType());
        skeleton.setAssignedId(perceptionType.getId().getValue());
        skeleton.setSupplierAssignedId(perceptionType.getAgentParty().getPartyIdentification().get(0).getIDValue());
        skeleton.setSupplierName(perceptionType.getAgentParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCustomerAssignedId(perceptionType.getReceiverParty().getPartyIdentification().get(0).getIDValue());
        skeleton.setCustomerName(perceptionType.getReceiverParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCurrency(perceptionType.getSunatTotalCashed().getCurrencyID());
        skeleton.setAmount(perceptionType.getSunatTotalCashed().getValue().floatValue());
        skeleton.setIssueDate(perceptionType.getIssueDate().getValue().toGregorianCalendar().getTime());

        // Postal address
        AddressType supplierPostalAddressType = perceptionType.getAgentParty().getPostalAddress();
        skeleton.setSupplierStreetAddress(supplierPostalAddressType.getStreetName().getValue());
        skeleton.setSupplierCity(supplierPostalAddressType.getCitySubdivisionName().getValue() + ", " + supplierPostalAddressType.getCityName().getValue() + ", " + supplierPostalAddressType.getCitySubdivisionName().getValue());
        skeleton.setSupplierCountry(supplierPostalAddressType.getCountry().getIdentificationCode().getValue());

        AddressType customerPostalAddressType = perceptionType.getReceiverParty().getPostalAddress();
        if (customerPostalAddressType != null) {
            skeleton.setCustomerStreetAddress(customerPostalAddressType.getStreetName().getValue());
            skeleton.setCustomerCity(customerPostalAddressType.getCitySubdivisionName().getValue() + ", " + customerPostalAddressType.getCityName().getValue() + ", " + customerPostalAddressType.getCitySubdivisionName().getValue());
            skeleton.setCustomerCountry(customerPostalAddressType.getCountry().getIdentificationCode().getValue());
        }

        return new ParsedDocument() {
            @Override
            public SkeletonDocument getSkeleton() {
                return skeleton;
            }

            @Override
            public Object getType() {
                return perceptionType;
            }
        };
    }

}
