package org.clarksnut.documents.jpa;

import org.apache.lucene.search.Sort;
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
import org.hibernate.search.elasticsearch.ElasticsearchQueries;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.sort.SortFieldContext;
import org.hibernate.search.query.engine.spi.QueryDescriptor;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Decorator
public class ESDocumentUserProvider implements DocumentUserProvider {

    private static final Logger logger = Logger.getLogger(ESDocumentUserProvider.class);

    @Inject
    @Delegate
    @Any
    private DocumentUserProvider delegate;

    @PersistenceContext
    private EntityManager em;

    private boolean isESEnabled;

    @PostConstruct
    public void init() {
        String indexManager = System.getenv("HIBERNATE_INDEX_MANAGER");
        if (indexManager != null && indexManager.equals("elasticsearch")) {
            isESEnabled = true;
        } else {
            isESEnabled = false;
        }
    }

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

    private BoolQueryBuilder getESQuery(org.clarksnut.documents.query.Query query, BoolQueryBuilder userBoolQuery) {
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

    @Override
    public DocumentUserModel getDocumentUser(UserModel user, String documentId) {
        return this.delegate.getDocumentUser(user, documentId);
    }

    @Override
    public List<DocumentUserModel> getDocumentsUser(UserModel user, DocumentUserQueryModel query, SpaceModel... space) {
        if (!isESEnabled) {
            return this.delegate.getDocumentsUser(user, query);
        }

        BoolQueryBuilder userQueryBuilder = getUserQuery(user, space);
        if (userQueryBuilder == null) {
            // User do not have any space assigned
            return Collections.emptyList();
        }
        BoolQueryBuilder searchQueryBuilder = getESQuery(query.getQuery(), userQueryBuilder);

        String esQuery = "{\"query\":" + searchQueryBuilder.toString() + "}";

        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(em);
        QueryDescriptor queryDescriptor = ElasticsearchQueries.fromJson(esQuery);

        Sort sort = null;
        if (query.getOrderBy() != null) {
            QueryBuilder queryBuilder;
            if (query.isForUser()) {
                queryBuilder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(DocumentUserEntity.class).get();
            } else {
                queryBuilder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(DocumentEntity.class).get();
            }

            SortFieldContext sortFieldContext = queryBuilder.sort().byField(QueryUtil.toJpaName(query.getOrderBy()));
            if (query.isAsc()) {
                sort = sortFieldContext.asc().createSort();
            } else {
                sort = sortFieldContext.desc().createSort();
            }
        }

        FullTextQuery fullTextQuery;
        if (query.isForUser()) {
            fullTextQuery = fullTextEm.createFullTextQuery(queryDescriptor, DocumentUserEntity.class);
        } else {
            fullTextQuery = fullTextEm.createFullTextQuery(queryDescriptor, DocumentEntity.class);
        }

        if (sort != null) {
            fullTextQuery.setSort(sort);
        }

        if (query.getOffset() != null && query.getOffset() != -1) {
            fullTextQuery.setFirstResult(query.getOffset());
        }
        if (query.getLimit() != null && query.getLimit() != -1) {
            fullTextQuery.setMaxResults(query.getLimit());
        }

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
        if (!isESEnabled) {
            return this.delegate.getDocumentsUserSize(user, query);
        }

        String esQuery = "{\"query\":" + QueryUtil.toESQueryBuilder(query.getQuery()).toString() + "}";

        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(em);
        QueryDescriptor queryDescriptor = ElasticsearchQueries.fromJson(esQuery);

        if (query.isForUser()) {
            return fullTextEm.createFullTextQuery(queryDescriptor, DocumentUserEntity.class).getResultSize();
        } else {
            return fullTextEm.createFullTextQuery(queryDescriptor, DocumentEntity.class).getResultSize();
        }
    }

}
