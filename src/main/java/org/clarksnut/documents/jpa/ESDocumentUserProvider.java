package org.clarksnut.documents.jpa;

import org.apache.lucene.search.Sort;
import org.clarksnut.documents.*;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.jpa.entity.DocumentUserEntity;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.query.SimpleQuery;
import org.clarksnut.query.es.ESQueryParser;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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

    public String getQuery(UserModel user, DocumentUserQueryModel query, SpaceModel... space) {
//        if (query.getDocumentFilters().isEmpty() && query.getUserDocumentFilters().isEmpty()) {
//            throw new IllegalStateException("Invalid query, at least one query should be requested");
//        }
//
//        // Space query
//        Set<String> userPermittedSpaceIds = getUserPermittedSpaces(user, space);
//        if (userPermittedSpaceIds.isEmpty()) {
//            return null;
//        }
//
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        for (SimpleQuery q : query.getDocumentFilters()) {
//            if (query.getDocumentFilters().isEmpty() || query.getUserDocumentFilters().isEmpty()) {
//                boolQueryBuilder.filter(ESQueryParser.toQueryBuilder(q, new DocumentFieldMapper()));
//            } else {
//                boolQueryBuilder.filter(ESQueryParser.toQueryBuilder(q, new DocumentFieldMapper("document")));
//            }
//        }
//        for (SimpleQuery q : query.getUserDocumentFilters()) {
//            boolQueryBuilder.filter(ESQueryParser.toQueryBuilder(q, new DocumentFieldMapper()));
//        }
//
//        DocumentFieldMapper fieldMapper = new DocumentFieldMapper();
//        boolQueryBuilder.should(QueryBuilders.termsQuery(fieldMapper.apply(DocumentModel.SUPPLIER_ASSIGNED_ID), userPermittedSpaceIds));
//        boolQueryBuilder.should(QueryBuilders.termsQuery(fieldMapper.apply(DocumentModel.CUSTOMER_ASSIGNED_ID), userPermittedSpaceIds));
//        boolQueryBuilder.minimumShouldMatch(1);
//        return boolQueryBuilder.toString();
        return null;
    }

    private Set<String> getUserPermittedSpaces(UserModel user, SpaceModel... space) {
        Set<String> allPermittedSpaceIds = user.getAllPermitedSpaces().stream().map(SpaceModel::getAssignedId).collect(Collectors.toSet());
        if (space != null && space.length > 0) {
            allPermittedSpaceIds.retainAll(Arrays.stream(space).map(SpaceModel::getAssignedId).collect(Collectors.toList()));
        }
        return allPermittedSpaceIds;
    }

    @Override
    public DocumentUserModel getDocumentUser(UserModel user, String documentId) {
        return this.delegate.getDocumentUser(user, documentId);
    }

    @Override
    public SearchResultModel<DocumentUserModel> getDocumentsUser(UserModel user, DocumentUserQueryModel query, SpaceModel... space) {
        if (!isESEnabled) {
            return this.delegate.getDocumentsUser(user, query);
        }

        final boolean isDocumentSearchOnly = query.getUserDocumentFilters().isEmpty();

        String esQuery = getQuery(user, query, space);

        // No results
        if (esQuery == null) {
            // User do not have any space assigned
            return new EmptySearchResultAdapter<>();
        }

        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(em);
        QueryDescriptor queryDescriptor = ElasticsearchQueries.fromJson("{\"query\":" + esQuery + "}");

        FullTextQuery fullTextQuery;
        if (isDocumentSearchOnly) {
            fullTextQuery = fullTextEm.createFullTextQuery(queryDescriptor, DocumentEntity.class);
        } else {
            fullTextQuery = fullTextEm.createFullTextQuery(queryDescriptor, DocumentUserEntity.class);
        }

        // Sort
        Sort sort = null;
        if (query.getOrderBy() != null) {
            QueryBuilder queryBuilder;
            if (isDocumentSearchOnly) {
                queryBuilder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(DocumentEntity.class).get();
            } else {
                queryBuilder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(DocumentUserEntity.class).get();
            }

            SortFieldContext sortFieldContext = queryBuilder.sort().byField(new DocumentFieldMapper().apply(query.getOrderBy()));
            if (query.isAsc()) {
                sort = sortFieldContext.asc().createSort();
            } else {
                sort = sortFieldContext.desc().createSort();
            }
        }

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
