package org.openfact.models.db.es.mapper.debitnote;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.debitnote_21.DebitNoteType;
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
@MapperType(value = "DebitNote")
public class DefaultDebitNoteReader implements UBLDocumentMapper {

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

        DebitNoteType debitNoteType = UBL21Reader.debitNote().read(document);
        persistCustomEntity(debitNoteType);

        UBLDocumentEntity entity = new UBLDocumentEntity();
        entity.setType("DebitNote");
        entity.setFileId(file.getId());
        entity.setAssignedId(debitNoteType != null ? debitNoteType.getIDValue() : null);
        return entity;
    }

    private void persistCustomEntity(DebitNoteType creditNoteType) {
        DefaultDebitNoteEntity creditNoteEntity = new DefaultDebitNoteEntity();

        creditNoteEntity.setId(OpenfactModelUtils.generateId());
        creditNoteEntity.setAssignedId(creditNoteType.getIDValue());
        if (creditNoteType.getIssueDateValue() != null) {
            creditNoteEntity.setIssueDate(creditNoteType.getIssueDateValue().toGregorianCalendar().getTime());
        }
        if (creditNoteType.getIssueTimeValue() != null) {
            creditNoteType.getIssueTimeValue().toGregorianCalendar().getTime();
        }
        creditNoteEntity.setTypeCode(creditNoteType.getPaymentAlternativeCurrencyCodeValue());
        creditNoteEntity.setCurrencyCode(creditNoteType.getDocumentCurrencyCodeValue());

        SupplierPartyType supplierPartyType = creditNoteType.getAccountingSupplierParty();
        if (supplierPartyType != null) {
            creditNoteEntity.setSupplierAssignedAccountId(supplierPartyType.getCustomerAssignedAccountIDValue());
        }

        MonetaryTotalType legalMonetaryTotalType = creditNoteType.getRequestedMonetaryTotal();
        if (legalMonetaryTotalType != null) {
            creditNoteEntity.setPayableAmount(legalMonetaryTotalType.getPayableAmountValue());
        }

        em.persist(creditNoteEntity);

        creditNoteType.getDebitNoteLine().forEach(lineType -> {
            DefaultDebitNoteLineEntity lineEntity = new DefaultDebitNoteLineEntity();
            lineEntity.setId(OpenfactModelUtils.generateId());
            lineEntity.setDebitNote(creditNoteEntity);
            lineEntity.setQuantity(lineEntity.getQuantity());
            lineEntity.setUnitCode(lineEntity.getUnitCode());
            lineEntity.setExtensionAmount(lineEntity.getExtensionAmount());
            lineEntity.setExtensionAmountCurrencyId(lineEntity.getExtensionAmountCurrencyId());
            em.persist(lineEntity);
        });
    }

}
