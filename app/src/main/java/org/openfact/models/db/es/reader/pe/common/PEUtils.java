package org.openfact.models.db.es.reader.pe.common;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import org.openfact.models.db.jpa.entity.SpaceEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class PEUtils {

    @Inject
    private EntityManager em;

    public SpaceEntity getSpace(SupplierPartyType supplierPartyType) {
        String assignedAccountIDValue = supplierPartyType.getCustomerAssignedAccountID().getValue();
        SpaceEntity spaceEntity = getSpaceEntityByAssignedId(assignedAccountIDValue);
        if (spaceEntity == null) {
            spaceEntity = new SpaceEntity();
            spaceEntity.setId(OpenfactModelUtils.generateId());
            spaceEntity.setAssignedId(supplierPartyType.getCustomerAssignedAccountID().getValue());
            spaceEntity.setName(supplierPartyType.getCustomerAssignedAccountID().getValue());
            em.persist(spaceEntity);
        }
        return spaceEntity;
    }

    public SpaceEntity getSpace(CustomerPartyType customerPartyType) {
        String assignedAccountIDValue = customerPartyType.getCustomerAssignedAccountID().getValue();
        SpaceEntity spaceEntity = getSpaceEntityByAssignedId(assignedAccountIDValue);
        if (spaceEntity == null) {
            spaceEntity = new SpaceEntity();
            spaceEntity.setId(OpenfactModelUtils.generateId());
            spaceEntity.setAssignedId(customerPartyType.getCustomerAssignedAccountID().getValue());
            spaceEntity.setName(customerPartyType.getCustomerAssignedAccountID().getValue());
            em.persist(spaceEntity);
        }
        return spaceEntity;
    }

    public SpaceEntity getSpace(PartyType agentParty) {
        String assignedAccountIDValue = agentParty.getPartyIdentification().get(0).getIDValue();
        SpaceEntity spaceEntity = getSpaceEntityByAssignedId(assignedAccountIDValue);
        if (spaceEntity == null) {
            spaceEntity = new SpaceEntity();
            spaceEntity.setId(OpenfactModelUtils.generateId());
            spaceEntity.setAssignedId(agentParty.getPartyIdentification().get(0).getIDValue());
            spaceEntity.setName(agentParty.getPartyIdentification().get(0).getIDValue());
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
