package org.clarksnut.documents.reader.basic.creditnote;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.jboss.logging.Logger;
import org.clarksnut.documents.DocumentReader;
import org.clarksnut.documents.GenericDocument;
import org.clarksnut.documents.reader.SupportedType;
import org.clarksnut.files.XmlUBLFileModel;

import javax.ejb.Stateless;
import java.math.BigDecimal;

@Stateless
@SupportedType(value = "CreditNote")
public class BasicCreditNoteReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(BasicCreditNoteReader.class);

    @Override
    public String getSupportedDocumentType() {
        return "CreditNote";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        CreditNoteType creditNoteType = UBL21Reader.creditNote().read(file.getDocument());
        if (creditNoteType == null) {
            return null;
        }

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setAssignedId(creditNoteType.getIDValue());
        documentEntity.setSupplierAssignedId(creditNoteType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setSupplierName(creditNoteType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCustomerAssignedId(creditNoteType.getAccountingCustomerParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setCustomerName(creditNoteType.getAccountingCustomerParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCurrency(creditNoteType.getLegalMonetaryTotal().getPayableAmount().getCurrencyID());
        documentEntity.setAmount(creditNoteType.getLegalMonetaryTotal().getPayableAmount().getValue().floatValue());
        documentEntity.setIssueDate(creditNoteType.getIssueDate().getValue().toGregorianCalendar().getTime());

        // Tax
        documentEntity.setTax(creditNoteType.getTaxTotal().stream()
                .map(TaxTotalType::getTaxAmountValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue()
        );

        // Postal address
        AddressType supplierPostalAddressType = creditNoteType.getAccountingSupplierParty().getParty().getPostalAddress();
        documentEntity.setSupplierStreetAddress(supplierPostalAddressType.getStreetNameValue());
        documentEntity.setSupplierCity(supplierPostalAddressType.getCitySubdivisionNameValue() + ", " + supplierPostalAddressType.getCityNameValue() + ", " + supplierPostalAddressType.getCitySubdivisionNameValue());
        documentEntity.setSupplierCountry(supplierPostalAddressType.getCountry().getIdentificationCodeValue());

        AddressType customerPostalAddressType = creditNoteType.getAccountingCustomerParty().getParty().getPostalAddress();
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
                return creditNoteType;
            }
        };
    }

}
