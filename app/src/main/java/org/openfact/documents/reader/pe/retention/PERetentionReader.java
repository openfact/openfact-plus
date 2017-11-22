package org.openfact.documents.reader.pe.retention;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import org.jboss.logging.Logger;
import org.openfact.documents.DocumentReader;
import org.openfact.documents.GenericDocument;
import org.openfact.documents.jpa.entity.DocumentEntity;
import org.openfact.documents.reader.SupportedType;
import org.openfact.documents.reader.pe.common.jaxb.retention.RetentionType;
import org.openfact.files.XmlUBLFileModel;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@SupportedType(value = "Retention")
public class PERetentionReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(PERetentionReader.class);

    @Override
    public String getSupportedDocumentType() {
        return "Retention";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        RetentionType retentionType;
        try {
            retentionType = OpenfactModelUtils.unmarshall(file.getDocument(), RetentionType.class);
        } catch (JAXBException e) {
            return null;
        }

        DocumentEntity documentEntity = new DocumentEntity();

        documentEntity.setAssignedId(retentionType.getId().getValue());
        documentEntity.setSupplierAssignedId(retentionType.getAgentParty().getPartyIdentification().get(0).getIDValue());
        documentEntity.setSupplierName(retentionType.getAgentParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCustomerAssignedId(retentionType.getReceiverParty().getPartyIdentification().get(0).getIDValue());
        documentEntity.setCustomerName(retentionType.getReceiverParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCurrency(retentionType.getSunatTotalPaid().getCurrencyID());
        documentEntity.setAmount(retentionType.getSunatTotalPaid().getValue().floatValue());
        documentEntity.setIssueDate(retentionType.getIssueDate().getValue().toGregorianCalendar().getTime());

        // Postal address
        AddressType supplierPostalAddressType = retentionType.getAgentParty().getPostalAddress();
        documentEntity.setSupplierStreetAddress(supplierPostalAddressType.getStreetName().getValue());
        documentEntity.setSupplierCity(supplierPostalAddressType.getCitySubdivisionName().getValue() + ", " + supplierPostalAddressType.getCityName().getValue() + ", " + supplierPostalAddressType.getCitySubdivisionName().getValue());
        documentEntity.setSupplierCountry(supplierPostalAddressType.getCountry().getIdentificationCode().getValue());

        AddressType customerPostalAddressType = retentionType.getReceiverParty().getPostalAddress();
        documentEntity.setCustomerStreetAddress(customerPostalAddressType.getStreetName().getValue());
        documentEntity.setCustomerCity(customerPostalAddressType.getCitySubdivisionName().getValue() + ", " + customerPostalAddressType.getCityName().getValue() + ", " + customerPostalAddressType.getCitySubdivisionName().getValue());
        documentEntity.setCustomerCountry(customerPostalAddressType.getCountry().getIdentificationCode().getValue());

        return new GenericDocument() {
            @Override
            public DocumentEntity getEntity() {
                return documentEntity;
            }

            @Override
            public Object getJaxb() {
                return retentionType;
            }
        };
    }

}
