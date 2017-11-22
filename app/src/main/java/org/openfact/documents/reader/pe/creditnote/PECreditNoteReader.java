package org.openfact.documents.reader.pe.creditnote;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import org.jboss.logging.Logger;
import org.openfact.documents.DocumentReader;
import org.openfact.documents.GenericDocument;
import org.openfact.documents.jpa.entity.DocumentEntity;
import org.openfact.documents.reader.SupportedType;
import org.openfact.files.XmlUBLFileModel;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import java.math.BigDecimal;

@Stateless
@SupportedType(value = "CreditNote")
public class PECreditNoteReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(PECreditNoteReader.class);

    @Override
    public String getSupportedDocumentType() {
        return "CreditNote";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        CreditNoteType creditNoteType;
        try {
            creditNoteType = OpenfactModelUtils.unmarshall(file.getDocument(), CreditNoteType.class);
        } catch (JAXBException e) {
            return null;
        }

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setAssignedId(creditNoteType.getID().getValue());
        documentEntity.setSupplierAssignedId(creditNoteType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setSupplierName(creditNoteType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCustomerAssignedId(creditNoteType.getAccountingCustomerParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setCustomerName(creditNoteType.getAccountingCustomerParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCurrency(creditNoteType.getLegalMonetaryTotal().getPayableAmount().getCurrencyID().value());
        documentEntity.setAmount(creditNoteType.getLegalMonetaryTotal().getPayableAmount().getValue().floatValue());
        documentEntity.setIssueDate(creditNoteType.getIssueDate().getValue().toGregorianCalendar().getTime());

        // Tax
        documentEntity.setTax(creditNoteType.getTaxTotal().stream()
                .map(f -> f.getTaxAmount().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue()
        );

        // Postal address
        AddressType postalAddressType = creditNoteType.getAccountingSupplierParty().getParty().getPostalAddress();
        documentEntity.setSupplierStreetAddress(postalAddressType.getStreetName().getValue());
        documentEntity.setSupplierCity(postalAddressType.getCitySubdivisionName().getValue() + ", " + postalAddressType.getCityName().getValue() + ", " + postalAddressType.getCitySubdivisionName().getValue());
        documentEntity.setSupplierCountry(postalAddressType.getCountry().getIdentificationCode().getValue());

        AddressType customerPostalAddressType = creditNoteType.getAccountingCustomerParty().getParty().getPostalAddress();
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
                return creditNoteType;
            }
        };
    }

}
