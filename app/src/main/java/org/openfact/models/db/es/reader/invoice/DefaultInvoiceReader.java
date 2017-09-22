package org.openfact.models.db.es.reader.invoice;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.openfact.models.FileModel;
import org.openfact.models.ModelException;
import org.openfact.models.DocumentModel;
import org.openfact.models.db.es.DocumentReader;
import org.openfact.models.db.es.GenericDocument;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.openfact.models.db.es.reader.MapperType;
import org.openfact.models.db.jpa.SpaceAdapter;
import org.openfact.models.db.jpa.UserAdapter;
import org.openfact.models.db.jpa.entity.SpaceEntity;
import org.openfact.models.db.jpa.entity.UserEntity;
import org.openfact.models.utils.OpenfactModelUtils;
import org.w3c.dom.Document;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
@MapperType(value = "Invoice")
public class DefaultInvoiceReader implements DocumentReader {

    @Inject
    private EntityManager em;

    @Override
    public GenericDocument read(FileModel file) {
        InvoiceType invoiceType;
        try {
            byte[] bytes = file.getFile();
            Document document = OpenfactModelUtils.toDocument(bytes);
            invoiceType = UBL21Reader.invoice().read(document);
        } catch (Exception e) {
            throw new ModelException("Could not read bytes from file");
        }
        if (invoiceType == null) {
            return null;
        }

        SpaceEntity spaceEntity = getSpace(invoiceType);
        if (spaceEntity == null) {
            spaceEntity = new SpaceEntity();
            spaceEntity.setId(OpenfactModelUtils.generateId());
            spaceEntity.setAssignedId(invoiceType.getAccountingSupplierParty().getCustomerAssignedAccountIDValue());
            spaceEntity.setName(invoiceType.getAccountingSupplierParty().getCustomerAssignedAccountIDValue());
            em.persist(spaceEntity);
        }

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setFileId(file.getId());
        documentEntity.setAssignedId(invoiceType.getIDValue());
        documentEntity.setSpace(spaceEntity);

        return new GenericDocument() {
            @Override
            public DocumentEntity getEntity() {
                return documentEntity;
            }

            @Override
            public Object getType() {
                return invoiceType;
            }
        };
    }

    private SpaceEntity getSpace(InvoiceType invoiceType) {
        SupplierPartyType accountingSupplierParty = invoiceType.getAccountingSupplierParty();
        if (accountingSupplierParty == null) return null;

        String assignedAccountIDValue = accountingSupplierParty.getCustomerAssignedAccountIDValue();
        TypedQuery<SpaceEntity> typedQuery = em.createNamedQuery("getSpaceByAssignedId", SpaceEntity.class);
        typedQuery.setParameter("assignedId", assignedAccountIDValue);
        List<SpaceEntity> resultList = typedQuery.getResultList();
        if (resultList.isEmpty()) return null;
        return resultList.get(0);
    }

}
