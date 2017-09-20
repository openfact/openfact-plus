package org.openfact.models.db.es.mapper.invoice;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.openfact.models.FileModel;
import org.openfact.models.ModelException;
import org.openfact.models.db.es.UBLDocumentMapper;
import org.openfact.models.db.es.entity.UBLDocumentEntity;
import org.openfact.models.db.es.mapper.MapperType;
import org.openfact.models.utils.OpenfactModelUtils;
import org.w3c.dom.Document;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Stateless
@MapperType(value = "Invoice")
public class DefaultInvoiceReader implements UBLDocumentMapper {

    @Inject
    private EntityManager em;

    @Override
    public UBLDocumentEntity map(FileModel file) {
        byte[] bytes;
        Document document;
        try {
            bytes = file.getFile();
            document = OpenfactModelUtils.toDocument(bytes);
        } catch (Exception e) {
            throw new ModelException("Could not read bytes from file");
        }

        InvoiceType invoiceType = UBL21Reader.invoice().read(document);
        persistCustomEntity(invoiceType);

        UBLDocumentEntity entity = new UBLDocumentEntity();
        entity.setType("Invoice");
        entity.setFileId(file.getId());
        entity.setAssignedId(invoiceType != null ? invoiceType.getIDValue() : null);
        return entity;
    }

    private void persistCustomEntity(InvoiceType invoiceType) {
        DefaultInvoiceEntity invoiceEntity = new DefaultInvoiceEntity();

        invoiceEntity.setId(OpenfactModelUtils.generateId());
        invoiceEntity.setAssignedId(invoiceType.getIDValue());
        if (invoiceType.getIssueDateValue() != null) {
            invoiceEntity.setIssueDate(invoiceType.getIssueDateValue().toGregorianCalendar().getTime());
        }
        if (invoiceType.getIssueTimeValue() != null) {
            invoiceType.getIssueTimeValue().toGregorianCalendar().getTime();
        }
        invoiceEntity.setTypeCode(invoiceType.getInvoiceTypeCodeValue());
        invoiceEntity.setCurrencyCode(invoiceType.getDocumentCurrencyCodeValue());

        SupplierPartyType supplierPartyType = invoiceType.getAccountingSupplierParty();
        if (supplierPartyType != null) {
            invoiceEntity.setSupplierAssignedAccountId(supplierPartyType.getCustomerAssignedAccountIDValue());
        }

        MonetaryTotalType legalMonetaryTotalType = invoiceType.getLegalMonetaryTotal();
        if (legalMonetaryTotalType != null) {
            invoiceEntity.setPayableAmount(legalMonetaryTotalType.getPayableAmountValue());
        }

        em.persist(invoiceEntity);

        invoiceType.getInvoiceLine().forEach(lineType -> {
            DefaultInvoiceLineEntity lineEntity = new DefaultInvoiceLineEntity();
            lineEntity.setId(OpenfactModelUtils.generateId());
            lineEntity.setInvoice(invoiceEntity);
            lineEntity.setQuantity(lineEntity.getQuantity());
            lineEntity.setUnitCode(lineEntity.getUnitCode());
            lineEntity.setExtensionAmount(lineEntity.getExtensionAmount());
            lineEntity.setExtensionAmountCurrencyId(lineEntity.getExtensionAmountCurrencyId());
            em.persist(lineEntity);
        });
    }

}
