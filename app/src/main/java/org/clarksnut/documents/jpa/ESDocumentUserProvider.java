package org.clarksnut.documents.jpa;

import org.apache.lucene.search.Sort;
import org.clarksnut.documents.DocumentUserModel;
import org.clarksnut.documents.DocumentUserProvider;
import org.clarksnut.documents.DocumentUserQueryModel;
import org.clarksnut.documents.SearchResultModel;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.jpa.entity.DocumentUserEntity;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
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
import java.util.Collections;
import java.util.List;
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

    @Inject
    private ESDocumentUserQueryParser queryParser;

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

        String esQuery = queryParser.getQuery(user, query, space);

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

            SortFieldContext sortFieldContext = queryBuilder.sort().byField(SearchDocumentUserFields.toDocumentSearchField(query.getOrderBy()));
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
