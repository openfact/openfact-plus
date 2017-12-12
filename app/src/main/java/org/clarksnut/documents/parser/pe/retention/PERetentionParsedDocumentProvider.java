package org.clarksnut.documents.parser.pe.retention;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import org.clarksnut.documents.parser.ParsedDocument;
import org.clarksnut.documents.parser.ParsedDocumentProvider;
import org.clarksnut.documents.parser.SkeletonDocument;
import org.clarksnut.documents.parser.SupportedDocumentType;
import org.clarksnut.documents.parser.pe.PEUtils;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;
import org.openfact.retention.RetentionType;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@SupportedDocumentType(value = "Retention")
public class PERetentionParsedDocumentProvider implements ParsedDocumentProvider {

    private static final Logger logger = Logger.getLogger(PERetentionParsedDocumentProvider.class);

    @Override
    public String getSupportedDocumentType() {
        return "Retention";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public ParsedDocument read(XmlUBLFileModel file) {
        RetentionType retentionType;
        try {
            retentionType = ClarksnutModelUtils.unmarshall(file.getDocument(), RetentionType.class);
        } catch (JAXBException e) {
            return null;
        }

        SkeletonDocument skeleton = new SkeletonDocument();

        skeleton.setAssignedId(retentionType.getId().getValue());
        skeleton.setType(getSupportedDocumentType());
        skeleton.setSupplierAssignedId(retentionType.getAgentParty().getPartyIdentification().get(0).getIDValue());
        skeleton.setSupplierName(retentionType.getAgentParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCustomerAssignedId(retentionType.getReceiverParty().getPartyIdentification().get(0).getIDValue());
        skeleton.setCustomerName(retentionType.getReceiverParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCurrency(retentionType.getSunatTotalPaid().getCurrencyID());
        skeleton.setAmount(retentionType.getSunatTotalPaid().getValue().floatValue());
        skeleton.setIssueDate(retentionType.getIssueDate().getValue().toGregorianCalendar().getTime());

        // Postal address
        AddressType supplierPostalAddressType = retentionType.getAgentParty().getPostalAddress();
        if (supplierPostalAddressType != null) {
            skeleton.setSupplierStreetAddress(supplierPostalAddressType.getStreetName().getValue());
            skeleton.setSupplierCountry(supplierPostalAddressType.getCountry().getIdentificationCode().getValue());
            skeleton.setSupplierCity(PEUtils.toCityString(supplierPostalAddressType));
        }

        AddressType customerPostalAddressType = retentionType.getReceiverParty().getPostalAddress();
        if (customerPostalAddressType != null) {
            skeleton.setCustomerStreetAddress(customerPostalAddressType.getStreetName().getValue());
            skeleton.setCustomerCountry(customerPostalAddressType.getCountry().getIdentificationCode().getValue());
            skeleton.setCustomerCity(PEUtils.toCityString(customerPostalAddressType));
        }

        return new ParsedDocument() {
            @Override
            public SkeletonDocument getSkeleton() {
                return skeleton;
            }

            @Override
            public Object getType() {
                return retentionType;
            }
        };
    }

}
