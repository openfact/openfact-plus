package org.clarksnut.documents.parser.basic.creditnote;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import org.clarksnut.documents.parser.ParsedDocument;
import org.clarksnut.documents.parser.ParsedDocumentProvider;
import org.clarksnut.documents.parser.SkeletonDocument;
import org.clarksnut.documents.parser.SupportedDocumentType;
import org.clarksnut.documents.parser.basic.BasicUtils;
import org.clarksnut.files.XmlUBLFileModel;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.Optional;

@Stateless
@SupportedDocumentType(value = "CreditNote")
public class BasicCreditNoteParsedDocumentProvider implements ParsedDocumentProvider {

    private static final Logger logger = Logger.getLogger(BasicCreditNoteParsedDocumentProvider.class);

    @Override
    public String getSupportedDocumentType() {
        return "CreditNote";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public ParsedDocument read(XmlUBLFileModel file) {
        CreditNoteType creditNoteType = UBL21Reader.creditNote().read(file.getDocument());
        if (creditNoteType == null) {
            return null;
        }

        SkeletonDocument skeleton = new SkeletonDocument();
        skeleton.setType(getSupportedDocumentType());
        skeleton.setAssignedId(creditNoteType.getIDValue());
        skeleton.setSupplierAssignedId(creditNoteType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        skeleton.setSupplierName(creditNoteType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCustomerAssignedId(creditNoteType.getAccountingCustomerParty().getCustomerAssignedAccountID().getValue());
        skeleton.setCustomerName(creditNoteType.getAccountingCustomerParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCurrency(creditNoteType.getLegalMonetaryTotal().getPayableAmount().getCurrencyID());
        skeleton.setAmount(creditNoteType.getLegalMonetaryTotal().getPayableAmount().getValue().floatValue());
        skeleton.setIssueDate(BasicUtils.toDate(creditNoteType.getIssueDate(), Optional.ofNullable(creditNoteType.getIssueTime())));

        // Tax
        skeleton.setTax(creditNoteType.getTaxTotal().stream()
                .map(TaxTotalType::getTaxAmountValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue()
        );

        // Postal address
        AddressType supplierPostalAddressType = creditNoteType.getAccountingSupplierParty().getParty().getPostalAddress();
        if (supplierPostalAddressType != null) {
            skeleton.setSupplierStreetAddress(supplierPostalAddressType.getStreetNameValue());
            skeleton.setSupplierCountry(supplierPostalAddressType.getCountry().getIdentificationCodeValue());
            skeleton.setSupplierCity(BasicUtils.toCityString(supplierPostalAddressType));
        }

        AddressType customerPostalAddressType = creditNoteType.getAccountingCustomerParty().getParty().getPostalAddress();
        if (customerPostalAddressType != null) {
            skeleton.setCustomerStreetAddress(customerPostalAddressType.getStreetNameValue());
            skeleton.setCustomerCountry(customerPostalAddressType.getCountry().getIdentificationCodeValue());
            skeleton.setCustomerCity(BasicUtils.toCityString(customerPostalAddressType));
        }

        return new ParsedDocument() {
            @Override
            public SkeletonDocument getSkeleton() {
                return skeleton;
            }

            @Override
            public Object getType() {
                return creditNoteType;
            }
        };
    }

}
