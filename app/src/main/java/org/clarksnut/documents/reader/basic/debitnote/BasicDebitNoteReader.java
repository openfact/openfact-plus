package org.clarksnut.documents.reader.basic.debitnote;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.debitnote_21.DebitNoteType;
import org.clarksnut.documents.DocumentReader;
import org.clarksnut.documents.GenericDocument;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.reader.SupportedType;
import org.clarksnut.files.XmlUBLFileModel;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import java.math.BigDecimal;

@Stateless
@SupportedType(value = "DebitNote")
public class BasicDebitNoteReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(BasicDebitNoteReader.class);

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

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setAssignedId(debitNoteType.getIDValue());
        documentEntity.setSupplierAssignedId(debitNoteType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setSupplierName(debitNoteType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCustomerAssignedId(debitNoteType.getAccountingCustomerParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setCustomerName(debitNoteType.getAccountingCustomerParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCurrency(debitNoteType.getRequestedMonetaryTotal().getPayableAmount().getCurrencyID());
        documentEntity.setAmount(debitNoteType.getRequestedMonetaryTotal().getPayableAmount().getValue().floatValue());
        documentEntity.setIssueDate(debitNoteType.getIssueDate().getValue().toGregorianCalendar().getTime());

        // Tax
        documentEntity.setTax(debitNoteType.getTaxTotal().stream()
                .map(TaxTotalType::getTaxAmountValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue()
        );

        // Postal address
        AddressType supplierPostalAddressType = debitNoteType.getAccountingSupplierParty().getParty().getPostalAddress();
        documentEntity.setSupplierStreetAddress(supplierPostalAddressType.getStreetNameValue());
        documentEntity.setSupplierCity(supplierPostalAddressType.getCitySubdivisionNameValue() + ", " + supplierPostalAddressType.getCityNameValue() + ", " + supplierPostalAddressType.getCitySubdivisionNameValue());
        documentEntity.setSupplierCountry(supplierPostalAddressType.getCountry().getIdentificationCodeValue());

        AddressType customerPostalAddressType = debitNoteType.getAccountingCustomerParty().getParty().getPostalAddress();
        if (customerPostalAddressType != null) {
            documentEntity.setCustomerStreetAddress(customerPostalAddressType.getStreetNameValue());
            documentEntity.setCustomerCity(customerPostalAddressType.getCitySubdivisionNameValue() + ", " + customerPostalAddressType.getCityNameValue() + ", " + customerPostalAddressType.getCitySubdivisionNameValue());
            documentEntity.setCustomerCountry(customerPostalAddressType.getCountry().getIdentificationCodeValue());
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
