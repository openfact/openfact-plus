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

}
