package org.clarksnut.documents.reader.basic.invoice;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.clarksnut.documents.DocumentReader;
import org.clarksnut.documents.GenericDocument;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.jboss.logging.Logger;
import org.clarksnut.documents.reader.SupportedType;
import org.clarksnut.files.XmlUBLFileModel;

import javax.ejb.Stateless;
import java.math.BigDecimal;

@Stateless
@SupportedType(value = "Invoice")
public class BasicInvoiceReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(BasicInvoiceReader.class);

    @Override
    public String getSupportedDocumentType() {
        return "Invoice";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        InvoiceType invoiceType = UBL21Reader.invoice().read(file.getDocument());
        if (invoiceType == null) {
            return null;
        }

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setAssignedId(invoiceType.getIDValue());
        documentEntity.setSupplierAssignedId(invoiceType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setSupplierName(invoiceType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCustomerAssignedId(invoiceType.getAccountingCustomerParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setCustomerName(invoiceType.getAccountingCustomerParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCurrency(invoiceType.getLegalMonetaryTotal().getPayableAmount().getCurrencyID());
        documentEntity.setAmount(invoiceType.getLegalMonetaryTotal().getPayableAmount().getValue().floatValue());
        documentEntity.setIssueDate(invoiceType.getIssueDate().getValue().toGregorianCalendar().getTime());

        // Tax
        documentEntity.setTax(invoiceType.getTaxTotal().stream()
                .map(TaxTotalType::getTaxAmountValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue()
        );

        // Postal Address
        AddressType supplierPostalAddressType = invoiceType.getAccountingSupplierParty().getParty().getPostalAddress();
        documentEntity.setSupplierStreetAddress(supplierPostalAddressType.getStreetNameValue());
        documentEntity.setSupplierCity(supplierPostalAddressType.getCitySubdivisionNameValue() + ", " + supplierPostalAddressType.getCityNameValue() + ", " + supplierPostalAddressType.getCitySubdivisionNameValue());
        documentEntity.setSupplierCountry(supplierPostalAddressType.getCountry().getIdentificationCodeValue());

        AddressType customerPostalAddressType = invoiceType.getAccountingCustomerParty().getParty().getPostalAddress();
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
                return invoiceType;
            }
        };
    }

}
