package org.clarksnut.models.jpa;

import org.clarksnut.models.DocumentModel;
import org.clarksnut.models.PartyModel;
import org.clarksnut.models.PartyProvider;
import org.clarksnut.models.jpa.entity.DocumentEntity;
import org.clarksnut.models.jpa.entity.PartyEntity;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Stateless
public class JpaPartyObservers {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private PartyProvider partyProvider;

    public void index(@Observes DocumentModel.DocumentCreationEvent documentCreationEvent) {
        DocumentModel document = documentCreationEvent.getCreatedDocument();
        DocumentEntity documentEntity = DocumentAdapter.toEntity(document, em);

        String supplierAssignedId = documentEntity.getSupplierAssignedId();
        String customerAssignedId = documentEntity.getCustomerAssignedId();
        processParty(supplierAssignedId, documentEntity.getSupplierName(), supplierAssignedId, customerAssignedId);

        if (documentEntity.getCustomerAssignedId() != null) {
            processParty(customerAssignedId, documentEntity.getCustomerName(), supplierAssignedId, customerAssignedId);
        }
    }

    private void processParty(String assignedId, String partyName, String supplierAssignedId, String customerAssignedId) {
        PartyModel party = partyProvider.getPartyByAssignedId(assignedId);
        if (party != null) {
            PartyEntity entity = IndexedPartyAdapter.toEntity(party, em);

            Set<String> newPartyName = new HashSet<>(Arrays.asList(partyName.split(" ")));
            if (!entity.getNames().containsAll(newPartyName)) {
                newPartyName.addAll(entity.getNames());
                entity.setNames(newPartyName);
            }

            Set<String> spaceIds = entity.getSpaceIds();
            if (supplierAssignedId!= null && !supplierAssignedId.trim().isEmpty() && !spaceIds.contains(supplierAssignedId)) {
                spaceIds.add(supplierAssignedId);
            }
            if (customerAssignedId != null && !customerAssignedId.trim().isEmpty() && !spaceIds.contains(customerAssignedId)) {
                spaceIds.add(customerAssignedId);
            }

            em.merge(entity);
        } else {
            PartyEntity entity = new PartyEntity();
            entity.setId(UUID.randomUUID().toString());
            entity.setAssignedId(assignedId);
            entity.setName(partyName);
            entity.setNames(new HashSet<>(Arrays.asList(partyName.split(" "))));
            entity.setSpaceIds(new HashSet<>(Arrays.asList(supplierAssignedId, customerAssignedId)));
            em.persist(entity);
        }
    }

}
