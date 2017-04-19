package org.openfact.services.managers;

import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.debitnote_21.DebitNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.jboss.logging.Logger;
import org.openfact.models.DocumentModel;
import org.openfact.models.DocumentProvider;
import org.openfact.models.ModelException;
import org.openfact.models.types.DocumentType;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class DocumentManager {

    protected static final Logger logger = Logger.getLogger(DocumentManager.class);

    @Inject
    private DocumentProvider model;

    public DocumentModel addDocument(InvoiceType invoiceType) throws ModelException {
        return model.addDocument(DocumentType.INVOICE.toString(), invoiceType.getIDValue());
    }

    public DocumentModel addDocument(CreditNoteType creditNoteType) throws ModelException {
        return model.addDocument(DocumentType.CREDIT_NOTE.toString(), creditNoteType.getIDValue());
    }

    public DocumentModel addDocument(DebitNoteType debitNoteType) throws ModelException {
        return model.addDocument(DocumentType.DEBIT_NOTE.toString(), debitNoteType.getIDValue());
    }

}
