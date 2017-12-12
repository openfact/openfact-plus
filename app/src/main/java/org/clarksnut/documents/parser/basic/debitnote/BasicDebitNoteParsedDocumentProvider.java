package org.clarksnut.documents.parser.basic.debitnote;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.debitnote_21.DebitNoteType;
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
@SupportedDocumentType(value = "DebitNote")
public class BasicDebitNoteParsedDocumentProvider implements ParsedDocumentProvider {

    private static final Logger logger = Logger.getLogger(BasicDebitNoteParsedDocumentProvider.class);

    @Override
    public String getSupportedDocumentType() {
        return "DebitNote";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public ParsedDocument read(XmlUBLFileModel file) {
        DebitNoteType debitNoteType = UBL21Reader.debitNote().read(file.getDocument());
        if (debitNoteType == null) {
            return null;
        }

        SkeletonDocument skeleton = new SkeletonDocument();
        skeleton.setType(getSupportedDocumentType());
        skeleton.setAssignedId(debitNoteType.getIDValue());
        skeleton.setSupplierAssignedId(debitNoteType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        skeleton.setSupplierName(debitNoteType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCustomerAssignedId(debitNoteType.getAccountingCustomerParty().getCustomerAssignedAccountID().getValue());
        skeleton.setCustomerName(debitNoteType.getAccountingCustomerParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCurrency(debitNoteType.getRequestedMonetaryTotal().getPayableAmount().getCurrencyID());
        skeleton.setAmount(debitNoteType.getRequestedMonetaryTotal().getPayableAmount().getValue().floatValue());
        skeleton.setIssueDate(BasicUtils.toDate(debitNoteType.getIssueDate(), Optional.ofNullable(debitNoteType.getIssueTime())));

        // Tax
        skeleton.setTax(debitNoteType.getTaxTotal().stream()
                .map(TaxTotalType::getTaxAmountValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue()
        );

        // Postal address
        AddressType supplierPostalAddressType = debitNoteType.getAccountingSupplierParty().getParty().getPostalAddress();
        if (supplierPostalAddressType != null) {
            skeleton.setSupplierStreetAddress(supplierPostalAddressType.getStreetNameValue());
            skeleton.setSupplierCountry(supplierPostalAddressType.getCountry().getIdentificationCodeValue());
            skeleton.setSupplierCity(BasicUtils.toCityString(supplierPostalAddressType));
        }

        AddressType customerPostalAddressType = debitNoteType.getAccountingCustomerParty().getParty().getPostalAddress();
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
                return debitNoteType;
            }
        };
    }

}
