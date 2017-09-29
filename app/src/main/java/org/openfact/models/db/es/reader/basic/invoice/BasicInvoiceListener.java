package org.openfact.models.db.es.reader.basic.invoice;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.openfact.models.DocumentModel;
import org.openfact.models.DocumentModel.DocumentCreationEvent;
import org.openfact.models.DocumentModel.DocumentRemovedEvent;
import org.openfact.models.db.es.DocumentAdapter;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.openfact.models.db.es.reader.MapperType;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Stateless
public class BasicInvoiceListener {

    @Inject
    private EntityManager em;

    public void creationListener(@Observes() @MapperType(value = "Invoice") DocumentCreationEvent createdDocument) {
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

    public void removeListener(@Observes() @MapperType(value = "Invoice") DocumentRemovedEvent removedDocument) {
//        DocumentModel document = removedDocument.getDocument();
//
//        TypedQuery<BasicInvoiceEntity> typedQuery = em.createNamedQuery("getInvoiceByDocumentId", BasicInvoiceEntity.class);
//        typedQuery.setParameter("documentId", document.getId());
//        typedQuery.getResultList().forEach(entity -> {
//            em.remove(entity);
//        });
    }

}
