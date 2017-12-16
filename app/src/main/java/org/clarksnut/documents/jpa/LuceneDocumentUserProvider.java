package org.clarksnut.documents.jpa;

import org.apache.lucene.search.Query;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentUserModel;
import org.clarksnut.documents.DocumentUserProvider;
import org.clarksnut.documents.DocumentUserQueryModel;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.jpa.entity.DocumentUserEntity;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Stateless
public class LuceneDocumentUserProvider implements DocumentUserProvider {

    private static final Logger logger = Logger.getLogger(LuceneDocumentUserProvider.class);

    @PersistenceContext
    private EntityManager em;

    private BoolQueryBuilder getUserQuery(UserModel user, SpaceModel... space) {
        Set<SpaceModel> allPermitedSpaces = user.getAllPermitedSpaces();
        if (space != null && space.length > 0) {
            allPermitedSpaces.retainAll(Arrays.asList(space));
        }
        if (!allPermitedSpaces.isEmpty()) {
            List<String> allSpacesIds = allPermitedSpaces.stream().map(SpaceModel::getAssignedId).collect(Collectors.toList());

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.should(QueryBuilders.termsQuery(DocumentModel.SUPPLIER_ASSIGNED_ID, allSpacesIds));
            boolQueryBuilder.should(QueryBuilders.termsQuery(DocumentModel.CUSTOMER_ASSIGNED_ID, allSpacesIds));
            boolQueryBuilder.minimumShouldMatch(1);

            return boolQueryBuilder;
        }
        return null;
    }

    private BoolQueryBuilder getESQuery(org.clarksnut.documents.query.Query query, UserModel user, SpaceModel... space) {
        BoolQueryBuilder userBoolQuery = getUserQuery(user, space);
        if (userBoolQuery == null) {
            return null;
        }

        org.elasticsearch.index.query.QueryBuilder queryBuilder = QueryUtil.toESQueryBuilder(query);
        if (queryBuilder instanceof BoolQueryBuilder) {
            return ((BoolQueryBuilder) queryBuilder)
                    .filter(userBoolQuery);
        } else {
            return QueryBuilders.boolQuery()
                    .must(userBoolQuery)
                    .filter(queryBuilder);
        }
    }

    private boolean isUserAllowedToSeeDocument(UserModel user, DocumentEntity documentEntity) {
        List<String> documentPermittedSpaceIds = Arrays.asList(
                documentEntity.getSupplierAssignedId(),
                documentEntity.getCustomerAssignedId()
        );

        List<String> userPermittedSpaceIds = user.getAllPermitedSpaces().stream()
                .map(SpaceModel::getAssignedId)
                .collect(Collectors.toList());

        userPermittedSpaceIds.retainAll(documentPermittedSpaceIds);

        return !userPermittedSpaceIds.isEmpty();
    }

    @Override
    public DocumentUserModel getDocumentUser(UserModel user, String documentId) {
        TypedQuery<DocumentUserEntity> typedQuery = em.createNamedQuery("getDocumentUserByUserAndDocument", DocumentUserEntity.class);
        typedQuery.setParameter("userId", user.getId());
        typedQuery.setParameter("documentId", documentId);

        List<DocumentUserEntity> resultList = typedQuery.getResultList();
        if (resultList.size() == 1) {
            DocumentUserEntity documentUserEntity = resultList.get(0);
            DocumentEntity documentEntity = documentUserEntity.getDocument();

            if (isUserAllowedToSeeDocument(user, documentEntity)) {
                return new DocumentUserAdapter(em, user, documentUserEntity);
            }
            return null;
        } else if (resultList.size() == 0) {
            DocumentEntity documentEntity = em.find(DocumentEntity.class, documentId);
            if (documentEntity == null) {
                return null;
            }

            if (!isUserAllowedToSeeDocument(user, documentEntity)) {
                return null;
            }

            DocumentUserEntity documentUserEntity = new DocumentUserEntity();
            documentUserEntity.setId(UUID.randomUUID().toString());
            documentUserEntity.setUserId(user.getId());
            documentUserEntity.setDocument(documentEntity);
            documentUserEntity.setStarred(false);
            documentUserEntity.setViewed(false);
            documentUserEntity.setChecked(false);
            em.persist(documentUserEntity);

            return new DocumentUserAdapter(em, user, documentUserEntity);
        } else {
            throw new IllegalStateException("Invalid number of results");
        }
    }

    @Override
    public List<DocumentUserModel> getDocumentsUser(UserModel user, DocumentUserQueryModel query, SpaceModel... space) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

        QueryBuilder queryBuilder;
        if (query.isForUser()) {
            queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(DocumentUserEntity.class).get();
        } else {
            queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(DocumentEntity.class).get();
        }

        BoolQueryBuilder userQueryBuilder = getUserQuery(user, space);

        Query luceneQuery = queryBuilder.keyword()
                .onField("history").boostedTo(3)
                .matching("storm")
                .createQuery();

        javax.persistence.Query fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery);

        //noinspection Duplicates
        if (query.isForUser()) {
            List<DocumentUserEntity> resultList = fullTextQuery.getResultList();
            return resultList.stream()
                    .map(f -> new DocumentUserAdapter(em, user, f))
                    .collect(Collectors.toList());
        } else {
            List<DocumentEntity> resultList = fullTextQuery.getResultList();
            return resultList.stream().map(documentEntity -> {
                DocumentUserModel documentUser = getDocumentUser(user, documentEntity.getId());
                if (documentUser == null) {
                    DocumentUserEntity documentUserEntity = new DocumentUserEntity();
                    documentUserEntity.setId(UUID.randomUUID().toString());
                    documentUserEntity.setDocument(documentEntity);
                    em.persist(documentUserEntity);
                    return new DocumentUserAdapter(em, user, documentUserEntity);
                }
                return documentUser;
            }).collect(Collectors.toList());
        }
    }

    @Override
    public int getDocumentsUserSize(UserModel user, DocumentUserQueryModel query) {
        return 0;
    }
}
