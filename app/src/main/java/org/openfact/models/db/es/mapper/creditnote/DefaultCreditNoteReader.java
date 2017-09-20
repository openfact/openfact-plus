package org.openfact.models.db.es.mapper.creditnote;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
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
@MapperType(value = "CreditNote")
public class DefaultCreditNoteReader implements UBLDocumentMapper {

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

        CreditNoteType creditNoteType = UBL21Reader.creditNote().read(document);
        persistCustomEntity(creditNoteType);

        UBLDocumentEntity entity = new UBLDocumentEntity();
        entity.setType("CreditNote");
        entity.setFileId(file.getId());
        entity.setAssignedId(creditNoteType != null ? creditNoteType.getIDValue() : null);
        return entity;
    }

    private void persistCustomEntity(CreditNoteType creditNoteType) {
        DefaultCreditNoteEntity creditNoteEntity = new DefaultCreditNoteEntity();

        creditNoteEntity.setId(OpenfactModelUtils.generateId());
        creditNoteEntity.setAssignedId(creditNoteType.getIDValue());
        if (creditNoteType.getIssueDateValue() != null) {
            creditNoteEntity.setIssueDate(creditNoteType.getIssueDateValue().toGregorianCalendar().getTime());
        }
        if (creditNoteType.getIssueTimeValue() != null) {
            creditNoteType.getIssueTimeValue().toGregorianCalendar().getTime();
        }
        creditNoteEntity.setTypeCode(creditNoteType.getCreditNoteTypeCodeValue());
        creditNoteEntity.setCurrencyCode(creditNoteType.getDocumentCurrencyCodeValue());

        SupplierPartyType supplierPartyType = creditNoteType.getAccountingSupplierParty();
        if (supplierPartyType != null) {
            creditNoteEntity.setSupplierAssignedAccountId(supplierPartyType.getCustomerAssignedAccountIDValue());
        }

        MonetaryTotalType legalMonetaryTotalType = creditNoteType.getLegalMonetaryTotal();
        if (legalMonetaryTotalType != null) {
            creditNoteEntity.setPayableAmount(legalMonetaryTotalType.getPayableAmountValue());
        }

        em.persist(creditNoteEntity);

        creditNoteType.getCreditNoteLine().forEach(lineType -> {
            DefaultCreditNoteLineEntity lineEntity = new DefaultCreditNoteLineEntity();
            lineEntity.setId(OpenfactModelUtils.generateId());
            lineEntity.setCreditNote(creditNoteEntity);
            lineEntity.setQuantity(lineEntity.getQuantity());
            lineEntity.setUnitCode(lineEntity.getUnitCode());
            lineEntity.setExtensionAmount(lineEntity.getExtensionAmount());
            lineEntity.setExtensionAmountCurrencyId(lineEntity.getExtensionAmountCurrencyId());
            em.persist(lineEntity);
        });
    }

}
