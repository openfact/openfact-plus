package org.openfact.services.managers;

import com.helger.xsds.ccts.cct.schemamodule.IdentifierType;
import com.helger.xsds.ccts.cct.schemamodule.TextType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.debitnote_21.DebitNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.jboss.logging.Logger;
import org.openfact.models.DocumentLineModel;
import org.openfact.models.DocumentModel;
import org.openfact.models.DocumentProvider;
import org.openfact.models.ModelException;
import org.openfact.models.types.DocumentType;
import org.openfact.ubl.pe.perception.PerceptionType;
import org.openfact.ubl.pe.perception.SUNATPerceptionDocumentReferenceType;
import org.openfact.ubl.pe.retention.RetentionType;
import org.openfact.ubl.pe.retention.SUNATRetentionDocumentReferenceType;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class DocumentManager {

    protected static final Logger logger = Logger.getLogger(DocumentManager.class);

    @Inject
    private DocumentProvider model;

    public DocumentModel addDocument(InvoiceType invoiceType, String originUuid) throws ModelException {
        String supplierAssignedAccountId = buildSupplierPartyAssignedAccountId(invoiceType);
        String customerAssignedAccountId = buildCustomerPartyAssignedAccountId(invoiceType);

        // Create Model
        DocumentModel document = model.addDocument(DocumentType.INVOICE.toString(), invoiceType.getIDValue(), supplierAssignedAccountId, originUuid);
        document.setCustomerAssignedAccountId(customerAssignedAccountId);
        document.setDocumentCurrencyCode(invoiceType.getDocumentCurrencyCodeValue());

        document.setAttribute("payableAmmount", invoiceType.getLegalMonetaryTotal().getPayableAmountValue());

        for (InvoiceLineType invoiceLineType : invoiceType.getInvoiceLine()) {
            DocumentLineModel documentLine = document.addDocumentLine();
            documentLine.setAttribute("description", invoiceLineType.getItem().getDescription()
                    .stream()
                    .map(TextType::getValue)
                    .collect(Collectors.joining(",")));
        }

        return document;
    }

    public DocumentModel addDocument(CreditNoteType creditNoteType, String originUuid) throws ModelException {
        String supplierAssignedAccountId = buildSupplierPartyAssignedAccountId(creditNoteType);
        String customerAssignedAccountId = buildCustomerPartyAssignedAccountId(creditNoteType);

        // Create Model
        DocumentModel document = model.addDocument(DocumentType.CREDIT_NOTE.toString(), creditNoteType.getIDValue(), supplierAssignedAccountId, originUuid);
        document.setCustomerAssignedAccountId(customerAssignedAccountId);
        document.setDocumentCurrencyCode(creditNoteType.getDocumentCurrencyCodeValue());

        document.setAttribute("payableAmmount", creditNoteType.getLegalMonetaryTotal().getPayableAmountValue());

        for (CreditNoteLineType creditNoteLineType : creditNoteType.getCreditNoteLine()) {
            DocumentLineModel documentLine = document.addDocumentLine();
            documentLine.setAttribute("description", creditNoteLineType.getItem().getDescription()
                    .stream()
                    .map(TextType::getValue)
                    .collect(Collectors.joining(",")));
        }

        return document;
    }

    public DocumentModel addDocument(DebitNoteType debitNoteType, String originUuid) throws ModelException {
        String supplierAssignedAccountId = buildSupplierPartyAssignedAccountId(debitNoteType);
        String customerAssignedAccountId = buildCustomerPartyAssignedAccountId(debitNoteType);

        // Create Model
        DocumentModel document = model.addDocument(DocumentType.CREDIT_NOTE.toString(), debitNoteType.getIDValue(), supplierAssignedAccountId, originUuid);
        document.setCustomerAssignedAccountId(customerAssignedAccountId);
        document.setDocumentCurrencyCode(debitNoteType.getDocumentCurrencyCodeValue());

        document.setAttribute("payableAmmount", debitNoteType.getRequestedMonetaryTotal().getPayableAmountValue());

        for (DebitNoteLineType debitNoteLineType : debitNoteType.getDebitNoteLine()) {
            DocumentLineModel documentLine = document.addDocumentLine();
            documentLine.setAttribute("description", debitNoteLineType.getItem().getDescription()
                    .stream()
                    .map(TextType::getValue)
                    .collect(Collectors.joining(",")));
        }

        return document;
    }

    public DocumentModel addDocument(PerceptionType perceptionType, String originUuid) throws ModelException {
        String supplierAssignedAccountId = buildSupplierPartyAssignedAccountId(perceptionType);
        String customerAssignedAccountId = buildCustomerPartyAssignedAccountId(perceptionType);

        // Create Model
        DocumentModel document = model.addDocument(DocumentType.PERCEPTION.toString(), perceptionType.getIdValue(), supplierAssignedAccountId, originUuid);
        document.setCustomerAssignedAccountId(customerAssignedAccountId);
        document.setAttribute("perceptionSystemCode", perceptionType.getSunatPerceptionSystemCode().getValue());
        document.setAttribute("retentionPercent", perceptionType.getSunatPerceptionPercent().getValue());
        document.setAttribute("totalCashed", perceptionType.getSunatTotalCashed().getValue());
        document.setAttribute("totalInvoiceAmount", perceptionType.getTotalInvoiceAmount().getValue());

        for (SUNATPerceptionDocumentReferenceType perceptionDocumentReferenceType : perceptionType.getSunatPerceptionDocumentReference()) {
            DocumentLineModel documentLine = document.addDocumentLine();
            documentLine.setAttribute("ID", perceptionDocumentReferenceType.getId().getValue());
            documentLine.setAttribute("schemeID", perceptionDocumentReferenceType.getId().getSchemeID());
            documentLine.setAttribute("payment", perceptionDocumentReferenceType.getPayment().getPaidAmount().getValue());
            documentLine.setAttribute("paymentCurrency", perceptionDocumentReferenceType.getPayment().getPaidAmount().getCurrencyID());
            documentLine.setAttribute("totalInvoiceAmount", perceptionDocumentReferenceType.getTotalInvoiceAmount().getValue());
            if (perceptionDocumentReferenceType.getSunatPerceptionInformation() != null) {
                if (perceptionDocumentReferenceType.getSunatPerceptionInformation().getExchangeRate() != null) {
                    documentLine.setAttribute("ExchangeRate-SourceCurrencyCode", perceptionDocumentReferenceType.getSunatPerceptionInformation().getExchangeRate().getSourceCurrencyCodeValue());
                    documentLine.setAttribute("ExchangeRate-TargetCurrencyCode", perceptionDocumentReferenceType.getSunatPerceptionInformation().getExchangeRate().getTargetCurrencyCodeValue());
                    documentLine.setAttribute("ExchangeRate-CalculateRate", perceptionDocumentReferenceType.getSunatPerceptionInformation().getExchangeRate().getCalculationRateValue());

                }
                documentLine.setAttribute("netTotalCashed", perceptionDocumentReferenceType.getSunatPerceptionInformation().getSunatNetTotalCashed().getValue());
                documentLine.setAttribute("netTotalCashedCurrency", perceptionDocumentReferenceType.getSunatPerceptionInformation().getSunatNetTotalCashed().getCurrencyID());
                documentLine.setAttribute("perceptionAmount", perceptionDocumentReferenceType.getSunatPerceptionInformation().getSunatPerceptionAmount().getValue());
                documentLine.setAttribute("perceptionAmountCurrency", perceptionDocumentReferenceType.getSunatPerceptionInformation().getSunatPerceptionAmount().getCurrencyID());
            }
        }

        return document;
    }

    public DocumentModel addDocument(RetentionType retentionType, String originUuid) throws ModelException {
        String supplierAssignedAccountId = buildSupplierPartyAssignedAccountId(retentionType);
        String customerAssignedAccountId = buildCustomerPartyAssignedAccountId(retentionType);

        // Create Model
        DocumentModel document = model.addDocument(DocumentType.RETENTION.toString(), retentionType.getIdValue(), supplierAssignedAccountId, originUuid);
        document.setCustomerAssignedAccountId(customerAssignedAccountId);
        document.setAttribute("retentionSystemCode", retentionType.getSunatRetentionSystemCode().getValue());
        document.setAttribute("retentionPercent", retentionType.getSunatRetentionPercent().getValue());
        document.setAttribute("totalPaid", retentionType.getSunatTotalPaid().getValue());
        document.setAttribute("totalInvoiceAmount", retentionType.getTotalInvoiceAmount().getValue());

        for (SUNATRetentionDocumentReferenceType retentionDocumentReferenceType : retentionType.getSunatRetentionDocumentReference()) {
            DocumentLineModel documentLine = document.addDocumentLine();
            documentLine.setAttribute("ID", retentionDocumentReferenceType.getID().getValue());
            documentLine.setAttribute("schemeID", retentionDocumentReferenceType.getID().getSchemeID());
            documentLine.setAttribute("payment", retentionDocumentReferenceType.getPayment().getPaidAmount().getValue());
            documentLine.setAttribute("paymentCurrency", retentionDocumentReferenceType.getPayment().getPaidAmount().getCurrencyID());
            documentLine.setAttribute("totalInvoiceAmount", retentionDocumentReferenceType.getTotalInvoiceAmount().getValue());
            if (retentionDocumentReferenceType.getSUNATRetentionInformation() != null) {
                if (retentionDocumentReferenceType.getSUNATRetentionInformation().getExchangeRate() != null) {
                    documentLine.setAttribute("ExchangeRate-SourceCurrencyCode", retentionDocumentReferenceType.getSUNATRetentionInformation().getExchangeRate().getSourceCurrencyCodeValue());
                    documentLine.setAttribute("ExchangeRate-TargetCurrencyCode", retentionDocumentReferenceType.getSUNATRetentionInformation().getExchangeRate().getTargetCurrencyCodeValue());
                    documentLine.setAttribute("ExchangeRate-CalculateRate", retentionDocumentReferenceType.getSUNATRetentionInformation().getExchangeRate().getCalculationRateValue());

                }
                documentLine.setAttribute("netTotalPaid", retentionDocumentReferenceType.getSUNATRetentionInformation().getSUNATNetTotalPaid().getValue());
                documentLine.setAttribute("netTotalPaidCurrency", retentionDocumentReferenceType.getSUNATRetentionInformation().getSUNATNetTotalPaid().getCurrencyID());
                documentLine.setAttribute("retentionAmount", retentionDocumentReferenceType.getSUNATRetentionInformation().getSUNATRetentionAmount().getValue());
                documentLine.setAttribute("retentionAmountCurrency", retentionDocumentReferenceType.getSUNATRetentionInformation().getSUNATRetentionAmount().getCurrencyID());
            }
        }

        return document;
    }


    public static String buildSupplierPartyAssignedAccountId(InvoiceType invoiceType) {
        SupplierPartyType accountingSupplierParty = invoiceType.getAccountingSupplierParty();
        Optional<String> supplierAssignedAccountIDValue = Optional.ofNullable(accountingSupplierParty.getCustomerAssignedAccountIDValue());
        String supplierAdditionalAccountId = accountingSupplierParty.getAdditionalAccountID()
                .stream()
                .map(IdentifierType::getValue)
                .collect(Collectors.joining("-"));
        return supplierAssignedAccountIDValue.orElseThrow(() -> new NullPointerException("Invalid Supplier customerAssignedAccountIDValue"))
                .concat("-")
                .concat(supplierAdditionalAccountId);
    }

    public static String buildCustomerPartyAssignedAccountId(InvoiceType invoiceType) {
        CustomerPartyType accountingCustomerParty = invoiceType.getAccountingCustomerParty();
        Optional<String> customerAssignedAccountIDValue = Optional.ofNullable(accountingCustomerParty.getCustomerAssignedAccountIDValue());
        String customerAdditionalAccountId = accountingCustomerParty.getAdditionalAccountID()
                .stream()
                .map(IdentifierType::getValue)
                .collect(Collectors.joining("-"));
        return customerAssignedAccountIDValue.orElseThrow(() -> new NullPointerException("Invalid Customer customerAssignedAccountIDValue"))
                .concat("-")
                .concat(customerAdditionalAccountId);
    }

    public static String buildSupplierPartyAssignedAccountId(CreditNoteType creditNoteType) {
        SupplierPartyType accountingSupplierParty = creditNoteType.getAccountingSupplierParty();
        Optional<String> supplierAssignedAccountIDValue = Optional.ofNullable(accountingSupplierParty.getCustomerAssignedAccountIDValue());
        String supplierAdditionalAccountId = accountingSupplierParty.getAdditionalAccountID()
                .stream()
                .map(IdentifierType::getValue)
                .collect(Collectors.joining("-"));
        return supplierAssignedAccountIDValue.orElseThrow(() -> new NullPointerException("Invalid Supplier customerAssignedAccountIDValue"))
                .concat("-")
                .concat(supplierAdditionalAccountId);
    }

    public static String buildCustomerPartyAssignedAccountId(CreditNoteType creditNoteType) {
        CustomerPartyType accountingCustomerParty = creditNoteType.getAccountingCustomerParty();
        Optional<String> customerAssignedAccountIDValue = Optional.ofNullable(accountingCustomerParty.getCustomerAssignedAccountIDValue());
        String customerAdditionalAccountId = accountingCustomerParty.getAdditionalAccountID()
                .stream()
                .map(IdentifierType::getValue)
                .collect(Collectors.joining("-"));
        return customerAssignedAccountIDValue.orElseThrow(() -> new NullPointerException("Invalid Customer customerAssignedAccountIDValue"))
                .concat("-")
                .concat(customerAdditionalAccountId);
    }

    public static String buildSupplierPartyAssignedAccountId(DebitNoteType debitNoteType) {
        SupplierPartyType accountingSupplierParty = debitNoteType.getAccountingSupplierParty();
        Optional<String> supplierAssignedAccountIDValue = Optional.ofNullable(accountingSupplierParty.getCustomerAssignedAccountIDValue());
        String supplierAdditionalAccountId = accountingSupplierParty.getAdditionalAccountID()
                .stream()
                .map(IdentifierType::getValue)
                .collect(Collectors.joining("-"));
        return supplierAssignedAccountIDValue.orElseThrow(() -> new NullPointerException("Invalid Supplier customerAssignedAccountIDValue"))
                .concat("-")
                .concat(supplierAdditionalAccountId);
    }

    public static String buildCustomerPartyAssignedAccountId(DebitNoteType debitNoteType) {
        CustomerPartyType accountingCustomerParty = debitNoteType.getAccountingCustomerParty();
        Optional<String> customerAssignedAccountIDValue = Optional.ofNullable(accountingCustomerParty.getCustomerAssignedAccountIDValue());
        String customerAdditionalAccountId = accountingCustomerParty.getAdditionalAccountID()
                .stream()
                .map(IdentifierType::getValue)
                .collect(Collectors.joining("-"));
        return customerAssignedAccountIDValue.orElseThrow(() -> new NullPointerException("Invalid Customer customerAssignedAccountIDValue"))
                .concat("-")
                .concat(customerAdditionalAccountId);
    }

    public static String buildSupplierPartyAssignedAccountId(PerceptionType perceptionType) {
        PartyType partyType = perceptionType.getReceiverParty();
        Optional<String> supplierAssignedAccountIDValue = Optional.ofNullable(partyType.getPartyIdentificationAtIndex(0).getIDValue());
        Optional<String> supplierAdditionalAccountId = Optional.ofNullable(partyType.getPartyIdentificationAtIndex(0).getID().getSchemeID());
               /* .stream()
                .map(IDType::getSchemeID)
                .collect(Collectors.joining("-"));*/
        return supplierAssignedAccountIDValue.orElseThrow(() -> new NullPointerException("Invalid Supplier supplierAssignedAccountIDValue"))
                .concat("-")
                .concat(supplierAdditionalAccountId.orElseThrow(() -> new NullPointerException("Invalid Supplier supplierAdditionalAccountId")));
    }

    public static String buildCustomerPartyAssignedAccountId(PerceptionType perceptionType) {
        PartyType accountingCustomerParty = perceptionType.getAgentParty();
        Optional<String> customerAssignedAccountIDValue = Optional.ofNullable(accountingCustomerParty.getPartyIdentificationAtIndex(0).getIDValue());
        Optional<String> customerAdditionalAccountId = Optional.ofNullable(accountingCustomerParty.getPartyIdentificationAtIndex(0).getID().getSchemeID());
               /* .stream()
                .map(IdentifierType::getValue)
                .collect(Collectors.joining("-"));*/
        return customerAssignedAccountIDValue.orElseThrow(() -> new NullPointerException("Invalid Customer customerAssignedAccountIDValue"))
                .concat("-")
                .concat(customerAdditionalAccountId.orElseThrow(() -> new NullPointerException("Invalid Customer customerAdditionalAccountId")));
    }

    public static String buildSupplierPartyAssignedAccountId(RetentionType retentionType) {
        PartyType partyType = retentionType.getReceiverParty();
        Optional<String> supplierAssignedAccountIDValue = Optional.ofNullable(partyType.getPartyIdentificationAtIndex(0).getIDValue());
        Optional<String> supplierAdditionalAccountId = Optional.ofNullable(partyType.getPartyIdentificationAtIndex(0).getID().getSchemeID());
               /* .stream()
                .map(IDType::getSchemeID)
                .collect(Collectors.joining("-"));*/
        return supplierAssignedAccountIDValue.orElseThrow(() -> new NullPointerException("Invalid Supplier supplierAssignedAccountIDValue"))
                .concat("-")
                .concat(supplierAdditionalAccountId.orElseThrow(() -> new NullPointerException("Invalid Supplier supplierAdditionalAccountId")));
    }

    public static String buildCustomerPartyAssignedAccountId(RetentionType retentionType) {
        PartyType accountingCustomerParty = retentionType.getAgentParty();
        Optional<String> customerAssignedAccountIDValue = Optional.ofNullable(accountingCustomerParty.getPartyIdentificationAtIndex(0).getIDValue());
        Optional<String> customerAdditionalAccountId = Optional.ofNullable(accountingCustomerParty.getPartyIdentificationAtIndex(0).getID().getSchemeID());
               /* .stream()
                .map(IdentifierType::getValue)
                .collect(Collectors.joining("-"));*/
        return customerAssignedAccountIDValue.orElseThrow(() -> new NullPointerException("Invalid Customer customerAssignedAccountIDValue"))
                .concat("-")
                .concat(customerAdditionalAccountId.orElseThrow(() -> new NullPointerException("Invalid Customer customerAdditionalAccountId")));
    }
}
