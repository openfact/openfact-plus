package org.clarksnut.documents.parser.pe.creditnote;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
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
@SupportedDocumentType(value = "CreditNote")
public class PECreditNoteParsedDocumentProvider implements ParsedDocumentProvider {

    private static final Logger logger = Logger.getLogger(PECreditNoteParsedDocumentProvider.class);

    @Override
    public String getSupportedDocumentType() {
        return "CreditNote";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public ParsedDocument read(XmlUBLFileModel file) {
        CreditNoteType creditNoteType;
        try {
            creditNoteType = ClarksnutModelUtils.unmarshall(file.getDocument(), CreditNoteType.class);
        } catch (JAXBException e) {
            return null;
        }

        SkeletonDocument skeleton = new SkeletonDocument();
        skeleton.setType(getSupportedDocumentType());
        skeleton.setAssignedId(creditNoteType.getID().getValue());
        skeleton.setSupplierAssignedId(creditNoteType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        skeleton.setSupplierName(creditNoteType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCustomerAssignedId(creditNoteType.getAccountingCustomerParty().getCustomerAssignedAccountID().getValue());
        skeleton.setCustomerName(creditNoteType.getAccountingCustomerParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setCurrency(creditNoteType.getLegalMonetaryTotal().getPayableAmount().getCurrencyID().value());
        skeleton.setAmount(creditNoteType.getLegalMonetaryTotal().getPayableAmount().getValue().floatValue());
        skeleton.setIssueDate(creditNoteType.getIssueDate().getValue().toGregorianCalendar().getTime());
        skeleton.setIssueDate(PEUtils.toDate(creditNoteType.getIssueDate(), Optional.ofNullable(creditNoteType.getIssueTime())));

        // Tax
        skeleton.setTax(creditNoteType.getTaxTotal().stream()
                .map(f -> f.getTaxAmount().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue()
        );

        // Postal address
        AddressType postalAddressType = creditNoteType.getAccountingSupplierParty().getParty().getPostalAddress();
        skeleton.setSupplierStreetAddress(postalAddressType.getStreetName().getValue());
        skeleton.setSupplierCity(postalAddressType.getCitySubdivisionName().getValue() + ", " + postalAddressType.getCityName().getValue() + ", " + postalAddressType.getCitySubdivisionName().getValue());
        skeleton.setSupplierCountry(postalAddressType.getCountry().getIdentificationCode().getValue());

        AddressType customerPostalAddressType = creditNoteType.getAccountingCustomerParty().getParty().getPostalAddress();
        if (customerPostalAddressType != null) {
            skeleton.setCustomerStreetAddress(customerPostalAddressType.getStreetName().getValue());
            skeleton.setCustomerCity(customerPostalAddressType.getCitySubdivisionName().getValue() + ", " + customerPostalAddressType.getCityName().getValue() + ", " + customerPostalAddressType.getCitySubdivisionName().getValue());
            skeleton.setCustomerCountry(customerPostalAddressType.getCountry().getIdentificationCode().getValue());
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
