package org.openfact.models.db.es.reader.basic.invoice;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.jboss.logging.Logger;
import org.openfact.models.*;
import org.openfact.models.db.es.DocumentReader;
import org.openfact.models.db.es.GenericDocument;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.openfact.models.db.es.reader.LocationType;
import org.openfact.models.db.es.reader.MapperType;
import org.openfact.models.db.es.reader.pe.invoice.PEInvoiceReader;
import org.openfact.models.db.jpa.entity.SpaceEntity;
import org.openfact.models.utils.OpenfactModelUtils;
import org.w3c.dom.Document;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
@MapperType(value = "Invoice")
@LocationType(value = "default")
public class DefaultInvoiceReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(DefaultInvoiceReader.class);

    @Inject
    private EntityManager em;

    @Override
    public GenericDocument read(XmlUblFileModel file) throws ModelFetchException, ModelParseException {
        byte[] bytes = file.getFile();

        Document document;
        try {
            document = OpenfactModelUtils.toDocument(bytes);
        } catch (Exception e) {
            logger.error("Could not parse document event when is " + XmlUblFileModel.class.getName());
            throw new ModelException("Could not read document");
        }

        InvoiceType invoiceType = UBL21Reader.invoice().read(document);
        if (invoiceType == null) {
            throw new ModelParseException("Could not parse document, it could be caused by invalid xml content");
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
