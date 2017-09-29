package org.openfact.models.db.es.reader.basic.common;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.openfact.models.db.jpa.entity.SpaceEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class BasicUtils {

    @Inject
    private EntityManager em;

    public SpaceEntity getSenderSpace(SupplierPartyType supplierPartyType) {
        String assignedAccountIDValue = supplierPartyType.getCustomerAssignedAccountIDValue();
        SpaceEntity spaceEntity = getSpaceEntityByAssignedId(assignedAccountIDValue);
        if (spaceEntity == null) {
            spaceEntity = new SpaceEntity();
            spaceEntity.setId(OpenfactModelUtils.generateId());
            spaceEntity.setAssignedId(supplierPartyType.getCustomerAssignedAccountIDValue());
            spaceEntity.setName(supplierPartyType.getCustomerAssignedAccountIDValue());
            em.persist(spaceEntity);
        }
        return spaceEntity;
    }

    public SpaceEntity getReceiverSpace(CustomerPartyType customerPartyType) {
        String assignedAccountIDValue = customerPartyType.getCustomerAssignedAccountIDValue();
        SpaceEntity spaceEntity = getSpaceEntityByAssignedId(assignedAccountIDValue);
        if (spaceEntity == null) {
            spaceEntity = new SpaceEntity();
            spaceEntity.setId(OpenfactModelUtils.generateId());
            spaceEntity.setAssignedId(customerPartyType.getCustomerAssignedAccountIDValue());
            spaceEntity.setName(customerPartyType.getCustomerAssignedAccountIDValue());
            em.persist(spaceEntity);
        }
        return spaceEntity;
    }

    private SpaceEntity getSpaceEntityByAssignedId(String assignedId) {
        TypedQuery<SpaceEntity> typedQuery = em.createNamedQuery("getSpaceByAssignedId", SpaceEntity.class);
        typedQuery.setParameter("assignedId", assignedId);
        List<SpaceEntity> resultList = typedQuery.getResultList();
        if (resultList.isEmpty()) return null;
        return resultList.get(0);
    }

}
