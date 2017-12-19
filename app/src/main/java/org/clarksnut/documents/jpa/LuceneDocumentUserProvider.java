package org.clarksnut.documents.jpa;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.clarksnut.documents.DocumentUserModel;
import org.clarksnut.documents.DocumentUserProvider;
import org.clarksnut.documents.DocumentUserQueryModel;
import org.clarksnut.documents.SearchResultModel;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.jpa.entity.DocumentUserEntity;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.sort.SortFieldContext;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Stateless
public class LuceneDocumentUserProvider implements DocumentUserProvider {

    private static final Logger logger = Logger.getLogger(LuceneDocumentUserProvider.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private LuceneDocumentUserQueryParser queryParser;

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
    public SearchResultModel<DocumentUserModel> getDocumentsUser(UserModel user, DocumentUserQueryModel query, SpaceModel... space) {
        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(em);

        final boolean isDocumentSearchOnly = query.getUserDocumentFilters().isEmpty();

        QueryBuilder queryBuilder;
        if (isDocumentSearchOnly) {
            queryBuilder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(DocumentEntity.class).get();
        } else {
            queryBuilder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(DocumentUserEntity.class).get();
        }

        Query luceneQuery = queryParser.getQuery(user, query, queryBuilder, space);

        // No results
        if (luceneQuery == null) {
            // User do not have any space assigned
            return new EmptySearchResultAdapter<>();
        }

        // Sort
        Sort sort = null;
        if (query.getOrderBy() != null) {
            SortFieldContext sortFieldContext = queryBuilder.sort().byField(SearchDocumentUserFields.toDocumentSearchField(query.getOrderBy()));
            if (query.isAsc()) {
                sort = sortFieldContext.asc().createSort();
            } else {
                sort = sortFieldContext.desc().createSort();
            }
        }

        FullTextQuery fullTextQuery = fullTextEm.createFullTextQuery(luceneQuery);

        if (sort != null) {
            fullTextQuery.setSort(sort);
        }

        // Pagination
        if (query.getOffset() != null && query.getOffset() != -1) {
            fullTextQuery.setFirstResult(query.getOffset());
        }
        if (query.getLimit() != null && query.getLimit() != -1) {
            fullTextQuery.setMaxResults(query.getLimit());
        }

        // Result
        List<DocumentUserModel> items;
        if (isDocumentSearchOnly) {
            List<DocumentEntity> resultList = fullTextQuery.getResultList();
            items = resultList.stream()
                    .map(documentEntity -> (DocumentUserModel) new DocumentUserAdapter(em, user, new DocumentUserEntity()))
                    .collect(Collectors.toList());
        } else {
            List<DocumentUserEntity> resultList = fullTextQuery.getResultList();
            items = resultList.stream()
                    .map(f -> new DocumentUserAdapter(em, user, f))
                    .collect(Collectors.toList());
        }

        return new SearchResultModel<DocumentUserModel>() {
            @Override
            public List<DocumentUserModel> getItems() {
                return items;
            }

            @Override
            public int getTotalResults() {
                return fullTextQuery.getResultSize();
            }
        };
    }

}
