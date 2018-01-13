package org.clarksnut.documents.jpa;

import org.clarksnut.documents.IndexedDocumentModel;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.jpa.entity.IndexedDocumentEntity;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractIndexedDocumentProvider {

    protected abstract EntityManager getEntityManager();

    protected Set<String> getUserPermittedSpaces(UserModel user, SpaceModel... space) {
        Set<String> allPermittedSpaceIds = user.getAllPermitedSpaces().stream().map(SpaceModel::getAssignedId).collect(Collectors.toSet());
        if (space != null && space.length > 0) {
            allPermittedSpaceIds.retainAll(Arrays.stream(space).map(SpaceModel::getAssignedId).collect(Collectors.toList()));
        }
        return allPermittedSpaceIds;
    }

    private boolean isUserAllowedToSeeDocument(UserModel user, IndexedDocumentEntity indexedDocumentEntity) {
        List<String> documentPermittedSpaceIds = Arrays.asList(
                indexedDocumentEntity.getSupplierAssignedId(),
                indexedDocumentEntity.getCustomerAssignedId()
        );

        List<String> userPermittedSpaceIds = user.getAllPermitedSpaces().stream()
                .map(SpaceModel::getAssignedId)
                .collect(Collectors.toList());

        userPermittedSpaceIds.retainAll(documentPermittedSpaceIds);

        return !userPermittedSpaceIds.isEmpty();
    }

    public IndexedDocumentModel getIndexedDocument(UserModel user, String documentId) {
        TypedQuery<IndexedDocumentEntity> typedQuery = getEntityManager().createNamedQuery("getIndexedDocumentById", IndexedDocumentEntity.class);
        typedQuery.setParameter("documentId", documentId);

        List<IndexedDocumentEntity> resultList = typedQuery.getResultList();
        if (resultList.size() == 1) {
            IndexedDocumentEntity indexedDocumentEntity = resultList.get(0);

            if (isUserAllowedToSeeDocument(user, indexedDocumentEntity)) {
                return new IndexedDocumentAdapter(getEntityManager(), user, indexedDocumentEntity);
            } else {
                return null;
            }
        } else if (resultList.size() == 0) {
            return null;
        } else {
            throw new IllegalStateException("Invalid number of results");
        }
    }

}
