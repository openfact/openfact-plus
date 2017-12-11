package org.clarksnut.documents.parser.basic.invoice;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.clarksnut.documents.parser.ParsedDocument;
import org.clarksnut.documents.parser.ParsedDocumentProvider;
import org.clarksnut.documents.parser.SkeletonDocument;
import org.clarksnut.documents.parser.SupportedDocumentType;
import org.clarksnut.files.XmlUBLFileModel;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import java.math.BigDecimal;

@Stateless
@SupportedDocumentType(value = "Invoice")
public class BasicInvoiceReader implements ParsedDocumentProvider {

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
    public ParsedDocument read(XmlUBLFileModel file) {
        InvoiceType invoiceType = UBL21Reader.invoice().read(file.getDocument());
        if (invoiceType == null) {
            return null;
        }

        SkeletonDocument skeleton = new SkeletonDocument();
        skeleton.setAssignedId(invoiceType.getIDValue());
        skeleton.setSupplierAssignedId(invoiceType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        skeleton.setSupplierName(invoiceType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCustomerAssignedId(invoiceType.getAccountingCustomerParty().getCustomerAssignedAccountID().getValue());
        skeleton.setCustomerName(invoiceType.getAccountingCustomerParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCurrency(invoiceType.getLegalMonetaryTotal().getPayableAmount().getCurrencyID());
        skeleton.setAmount(invoiceType.getLegalMonetaryTotal().getPayableAmount().getValue().floatValue());
        skeleton.setIssueDate(invoiceType.getIssueDate().getValue().toGregorianCalendar().getTime());

        // Tax
        skeleton.setTax(invoiceType.getTaxTotal().stream()
                .map(TaxTotalType::getTaxAmountValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue()
        );

        // Postal Address
        AddressType supplierPostalAddressType = invoiceType.getAccountingSupplierParty().getParty().getPostalAddress();
        skeleton.setSupplierStreetAddress(supplierPostalAddressType.getStreetNameValue());
        skeleton.setSupplierCity(supplierPostalAddressType.getCitySubdivisionNameValue() + ", " + supplierPostalAddressType.getCityNameValue() + ", " + supplierPostalAddressType.getCitySubdivisionNameValue());
        skeleton.setSupplierCountry(supplierPostalAddressType.getCountry().getIdentificationCodeValue());

        AddressType customerPostalAddressType = invoiceType.getAccountingCustomerParty().getParty().getPostalAddress();
        if (customerPostalAddressType != null) {
            skeleton.setCustomerStreetAddress(customerPostalAddressType.getStreetNameValue());
            skeleton.setCustomerCity(customerPostalAddressType.getCitySubdivisionNameValue() + ", " + customerPostalAddressType.getCityNameValue() + ", " + customerPostalAddressType.getCitySubdivisionNameValue());
            skeleton.setCustomerCountry(customerPostalAddressType.getCountry().getIdentificationCodeValue());
        }

        return new ParsedDocument() {
            @Override
            public SkeletonDocument getSkeleton() {
                return skeleton;
            }

            @Override
            public Object getType() {
                return invoiceType;
            }
        };
    }

}
