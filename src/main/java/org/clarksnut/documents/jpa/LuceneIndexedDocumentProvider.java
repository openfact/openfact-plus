package org.clarksnut.documents.jpa;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.clarksnut.documents.IndexedDocumentModel;
import org.clarksnut.documents.IndexedDocumentProvider;
import org.clarksnut.documents.IndexedDocumentQueryModel;
import org.clarksnut.documents.SearchResultModel;
import org.clarksnut.documents.jpa.IndexedManagerType.Type;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.jpa.entity.IndexedDocumentEntity;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.query.SimpleQuery;
import org.clarksnut.query.es.LuceneQueryParser;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.sort.SortFieldContext;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@IndexedManagerType(type = Type.LUCENE)
public class LuceneIndexedDocumentProvider extends AbstractIndexedDocumentProvider implements IndexedDocumentProvider {

    private static final Logger logger = Logger.getLogger(LuceneIndexedDocumentProvider.class);

    @PersistenceContext
    private EntityManager em;

    public org.apache.lucene.search.Query getQuery(UserModel user, IndexedDocumentQueryModel query, QueryBuilder queryBuilder, SpaceModel... space) {
        if (query.getFilters().isEmpty() && query.getFilters().isEmpty()) {
            throw new IllegalStateException("Invalid query, at least one query should be requested");
        }

        // Space query
        Set<String> userPermittedSpaceIds = getUserPermittedSpaces(user, space);
        if (userPermittedSpaceIds.isEmpty()) {
            return null;
        }

        BooleanJunction<BooleanJunction> boolQueryBuilder = queryBuilder.bool();
        for (SimpleQuery q : query.getFilters()) {
            boolQueryBuilder.must(LuceneQueryParser.toLuceneQuery(q, new DocumentFieldMapper(), queryBuilder));
        }

        DocumentFieldMapper fieldMapper = new DocumentFieldMapper();
        String permittedSpaceIdsString = userPermittedSpaceIds.stream().collect(Collectors.joining(" "));
        boolQueryBuilder.should(queryBuilder.keyword().onField(fieldMapper.apply(IndexedDocumentModel.SUPPLIER_ASSIGNED_ID)).matching(permittedSpaceIdsString).createQuery());
        boolQueryBuilder.should(queryBuilder.keyword().onField(fieldMapper.apply(IndexedDocumentModel.CUSTOMER_ASSIGNED_ID)).matching(permittedSpaceIdsString).createQuery());
        return boolQueryBuilder.createQuery();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public SearchResultModel<IndexedDocumentModel> getDocumentsUser(UserModel user, IndexedDocumentQueryModel query, SpaceModel... space) {
        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(em);

        QueryBuilder queryBuilder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(DocumentEntity.class).get();

        Query luceneQuery = getQuery(user, query, queryBuilder, space);

        // No results
        if (luceneQuery == null) {
            // User do not have any space assigned
            return new EmptySearchResultAdapter<>();
        }

        // Sort
        Sort sort = null;
        if (query.getOrderBy() != null) {
            SortFieldContext sortFieldContext = queryBuilder.sort().byField(new DocumentFieldMapper().apply(query.getOrderBy()));
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
        List<IndexedDocumentEntity> resultList = fullTextQuery.getResultList();
        List<IndexedDocumentModel> items = resultList.stream()
                .map(f -> new IndexedDocumentAdapter(em, f))
                .collect(Collectors.toList());

        return new SearchResultModel<IndexedDocumentModel>() {
            @Override
            public List<IndexedDocumentModel> getItems() {
                return items;
            }

            @Override
            public int getTotalResults() {
                return fullTextQuery.getResultSize();
            }
        };
    }
}
