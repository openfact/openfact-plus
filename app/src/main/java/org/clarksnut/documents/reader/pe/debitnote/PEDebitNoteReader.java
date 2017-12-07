package org.clarksnut.documents.reader.pe.debitnote;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import org.clarksnut.documents.DocumentReader;
import org.clarksnut.documents.GenericDocument;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.reader.SupportedType;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;
import org.clarksnut.files.XmlUBLFileModel;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import java.math.BigDecimal;

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
            debitNoteType = ClarksnutModelUtils.unmarshall(file.getDocument(), DebitNoteType.class);
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

        // Tax
        documentEntity.setTax(debitNoteType.getTaxTotal().stream()
                .map(f -> f.getTaxAmount().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue()
        );

        // Postal address
        AddressType postalAddressType = debitNoteType.getAccountingSupplierParty().getParty().getPostalAddress();
        documentEntity.setSupplierStreetAddress(postalAddressType.getStreetName().getValue());
        documentEntity.setSupplierCity(postalAddressType.getCitySubdivisionName().getValue() + ", " + postalAddressType.getCityName().getValue() + ", " + postalAddressType.getCitySubdivisionName().getValue());
        documentEntity.setSupplierCountry(postalAddressType.getCountry().getIdentificationCode().getValue());

        AddressType customerPostalAddressType = debitNoteType.getAccountingCustomerParty().getParty().getPostalAddress();
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
                return debitNoteType;
            }
        };
    }

}
