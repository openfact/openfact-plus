package org.clarksnut.documents.parser.pe.debitnote;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
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
@SupportedDocumentType(value = "DebitNote")
public class PEDebitNoteParsedDocumentProvider implements ParsedDocumentProvider {

    private static final Logger logger = Logger.getLogger(PEDebitNoteParsedDocumentProvider.class);

    @Override
    public String getSupportedDocumentType() {
        return "DebitNote";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public ParsedDocument read(XmlUBLFileModel file) {
        DebitNoteType debitNoteType;
        try {
            debitNoteType = ClarksnutModelUtils.unmarshall(file.getDocument(), DebitNoteType.class);
        } catch (JAXBException e) {
            return null;
        }

        SkeletonDocument skeleton = new SkeletonDocument();
        skeleton.setType(getSupportedDocumentType());
        skeleton.setAssignedId(debitNoteType.getID().getValue());
        skeleton.setSupplierAssignedId(debitNoteType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        skeleton.setSupplierName(debitNoteType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCustomerAssignedId(debitNoteType.getAccountingCustomerParty().getCustomerAssignedAccountID().getValue());
        skeleton.setCustomerName(debitNoteType.getAccountingCustomerParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCurrency(debitNoteType.getRequestedMonetaryTotal().getPayableAmount().getCurrencyID().value());
        skeleton.setAmount(debitNoteType.getRequestedMonetaryTotal().getPayableAmount().getValue().floatValue());
        skeleton.setIssueDate(PEUtils.toDate(debitNoteType.getIssueDate(), Optional.ofNullable(debitNoteType.getIssueTime())));

        // Tax
        skeleton.setTax(debitNoteType.getTaxTotal().stream()
                .map(f -> f.getTaxAmount().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue()
        );

        // Postal address
        AddressType postalAddressType = debitNoteType.getAccountingSupplierParty().getParty().getPostalAddress();
        if (postalAddressType != null) {
            skeleton.setSupplierStreetAddress(postalAddressType.getStreetName().getValue());
            skeleton.setSupplierCountry(postalAddressType.getCountry().getIdentificationCode().getValue());
            skeleton.setSupplierCity(PEUtils.toCityString(postalAddressType));
        }

        AddressType customerPostalAddressType = debitNoteType.getAccountingCustomerParty().getParty().getPostalAddress();
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
                return debitNoteType;
            }
        };
    }

}
