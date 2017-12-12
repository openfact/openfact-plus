package org.clarksnut.documents.parser.pe.invoice;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.clarksnut.documents.parser.ParsedDocument;
import org.clarksnut.documents.parser.ParsedDocumentProvider;
import org.clarksnut.documents.parser.SkeletonDocument;
import org.clarksnut.documents.parser.SupportedDocumentType;
import org.clarksnut.documents.parser.pe.PEUtils;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import java.math.BigDecimal;
import java.util.Optional;

@Stateless
@SupportedDocumentType(value = "Invoice")
public class PEInvoiceParsedDocumentProvider implements ParsedDocumentProvider {

    private static final Logger logger = Logger.getLogger(PEInvoiceParsedDocumentProvider.class);

    @Override
    public String getSupportedDocumentType() {
        return "Invoice";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public ParsedDocument read(XmlUBLFileModel file) {
        InvoiceType invoiceType;
        try {
            invoiceType = ClarksnutModelUtils.unmarshall(file.getDocument(), InvoiceType.class);
        } catch (JAXBException e) {
            return null;
        }

        SkeletonDocument skeleton = new SkeletonDocument();
        skeleton.setType(getSupportedDocumentType());
        skeleton.setAssignedId(invoiceType.getID().getValue());
        skeleton.setSupplierAssignedId(invoiceType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        skeleton.setSupplierName(invoiceType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCustomerAssignedId(invoiceType.getAccountingCustomerParty().getCustomerAssignedAccountID().getValue());
        skeleton.setCustomerName(invoiceType.getAccountingCustomerParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCurrency(invoiceType.getLegalMonetaryTotal().getPayableAmount().getCurrencyID().value());
        skeleton.setAmount(invoiceType.getLegalMonetaryTotal().getPayableAmount().getValue().floatValue());
        skeleton.setIssueDate(invoiceType.getIssueDate().getValue().toGregorianCalendar().getTime());
        skeleton.setIssueDate(PEUtils.toDate(invoiceType.getIssueDate(), Optional.ofNullable(invoiceType.getIssueTime())));

        // Tax
        skeleton.setTax(invoiceType.getTaxTotal().stream()
                .map(f -> f.getTaxAmount().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue()
        );

        // Postal address
        AddressType supplierPostalAddressType = invoiceType.getAccountingSupplierParty().getParty().getPostalAddress();
        if (supplierPostalAddressType != null) {
            skeleton.setSupplierStreetAddress(supplierPostalAddressType.getStreetName().getValue());
            skeleton.setSupplierCountry(supplierPostalAddressType.getCountry().getIdentificationCode().getValue());
            skeleton.setSupplierCity(PEUtils.toCityString(supplierPostalAddressType));
        }

        AddressType customerPostalAddressType = invoiceType.getAccountingCustomerParty().getParty().getPostalAddress();
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
                return invoiceType;
            }
        };
    }

}
