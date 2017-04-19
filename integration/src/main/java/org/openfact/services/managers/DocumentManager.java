package org.openfact.services.managers;

import com.helger.xsds.ccts.cct.schemamodule.IdentifierType;
import com.helger.xsds.ccts.cct.schemamodule.TextType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
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

    public DocumentModel addDocument(InvoiceType invoiceType) throws ModelException {
        // Supplier party
        SupplierPartyType accountingSupplierParty = invoiceType.getAccountingSupplierParty();
        Optional<String> supplierAssignedAccountIDValue = Optional.ofNullable(accountingSupplierParty.getCustomerAssignedAccountIDValue());
        String supplierAdditionalAccountId = accountingSupplierParty.getAdditionalAccountID()
                .stream()
                .map(IdentifierType::getValue)
                .collect(Collectors.joining("-"));
        String supplierAssignedAccountId = supplierAssignedAccountIDValue.orElseThrow(() -> new ModelException("Invalid Supplier customerAssignedAccountIDValue"))
                .concat("-")
                .concat(supplierAdditionalAccountId);

        // Customer party
        CustomerPartyType accountingCustomerParty = invoiceType.getAccountingCustomerParty();
        Optional<String> customerAssignedAccountIDValue = Optional.ofNullable(accountingCustomerParty.getCustomerAssignedAccountIDValue());
        String customerAdditionalAccountId = accountingSupplierParty.getAdditionalAccountID()
                .stream()
                .map(IdentifierType::getValue)
                .collect(Collectors.joining("-"));
        String customerAssignedAccountId = supplierAssignedAccountIDValue.orElseThrow(() -> new ModelException("Invalid Customer customerAssignedAccountIDValue"))
                .concat("-")
                .concat(customerAdditionalAccountId);

        // Create Model
        DocumentModel document = model.addDocument(DocumentType.INVOICE.toString(), invoiceType.getIDValue(), supplierAssignedAccountId);
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

    public DocumentModel addDocument(CreditNoteType creditNoteType) throws ModelException {
        return null;
    }

    public DocumentModel addDocument(DebitNoteType debitNoteType) throws ModelException {
        return null;
    }

}
