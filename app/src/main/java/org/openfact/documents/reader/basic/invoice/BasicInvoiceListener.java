package org.openfact.documents.reader.basic.invoice;

import org.openfact.documents.reader.SupportedType;
import org.openfact.documents.DocumentModel.DocumentCreationEvent;
import org.openfact.documents.DocumentModel.DocumentRemovedEvent;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Stateless
public class BasicInvoiceListener {

    @Inject
    private EntityManager em;

    public void creationListener(@Observes() @SupportedType(value = "Invoice") DocumentCreationEvent createdDocument) {
//        DocumentEntity documentEntity = DocumentAdapter.toEntity(createdDocument.getCreatedDocument(), em);
//        InvoiceType invoiceType = (InvoiceType) createdDocument.getJaxb();
//
//        BasicInvoiceEntity invoiceEntity = new BasicInvoiceEntity();
//        invoiceEntity.setId(OpenfactModelUtils.generateId());
//        invoiceEntity.setAssignedId(invoiceType.getIDValue());
//        invoiceEntity.setDocument(documentEntity);
//        if (invoiceType.getIssueDateValue() != null) {
//            invoiceEntity.setIssueDate(invoiceType.getIssueDateValue().toGregorianCalendar().getTime());
//        }
//        if (invoiceType.getIssueTimeValue() != null) {
//            invoiceType.getIssueTimeValue().toGregorianCalendar().getTime();
//        }
//        invoiceEntity.setTypeCode(invoiceType.getInvoiceTypeCodeValue());
//        invoiceEntity.setCurrencyCode(invoiceType.getDocumentCurrencyCodeValue());
//
//        SupplierPartyType supplierPartyType = invoiceType.getAccountingSupplierParty();
//        if (supplierPartyType != null) {
//            invoiceEntity.setSupplierAssignedAccountId(supplierPartyType.getCustomerAssignedAccountIDValue());
//        }
//
//        MonetaryTotalType legalMonetaryTotalType = invoiceType.getLegalMonetaryTotal();
//        if (legalMonetaryTotalType != null) {
//            invoiceEntity.setPayableAmount(legalMonetaryTotalType.getPayableAmountValue());
//        }
//
//        em.persist(invoiceEntity);
//
//        invoiceType.getInvoiceLine().forEach(lineType -> {
//            BasicInvoiceLineEntity lineEntity = new BasicInvoiceLineEntity();
//            lineEntity.setId(OpenfactModelUtils.generateId());
//            lineEntity.setInvoice(invoiceEntity);
//            lineEntity.setQuantity(lineEntity.getQuantity());
//            lineEntity.setUnitCode(lineEntity.getUnitCode());
//            lineEntity.setExtensionAmount(lineEntity.getExtensionAmount());
//            lineEntity.setExtensionAmountCurrencyId(lineEntity.getExtensionAmountCurrencyId());
//            em.persist(lineEntity);
//        });
    }

    public void removeListener(@Observes() @SupportedType(value = "Invoice") DocumentRemovedEvent removedDocument) {
//        DocumentModel document = removedDocument.getDocument();
//
//        TypedQuery<BasicInvoiceEntity> typedQuery = em.createNamedQuery("getInvoiceByDocumentId", BasicInvoiceEntity.class);
//        typedQuery.setParameter("documentId", document.getId());
//        typedQuery.getResultList().forEach(entity -> {
//            em.remove(entity);
//        });
    }

}
