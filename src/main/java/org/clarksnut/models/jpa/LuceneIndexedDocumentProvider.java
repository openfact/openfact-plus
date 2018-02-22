package org.clarksnut.models.jpa;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.clarksnut.models.IndexedDocumentModel;
import org.clarksnut.models.IndexedDocumentProvider;
import org.clarksnut.models.IndexedDocumentQueryModel;
import org.clarksnut.models.SearchResultModel;
import org.clarksnut.models.jpa.IndexedManagerType.Type;
import org.clarksnut.models.jpa.entity.IndexedDocumentEntity;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.query.es.LuceneQueryParser;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.sort.SortFieldContext;
import org.hibernate.search.query.engine.spi.FacetManager;
import org.hibernate.search.query.facet.Facet;
import org.hibernate.search.query.facet.FacetSortOrder;
import org.hibernate.search.query.facet.FacetingRequest;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
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
        DocumentFieldMapper fieldMapper = new DocumentFieldMapper();

        // Space query
        Set<String> userPermittedSpaceIds = getUserPermittedSpaces(user, space);
        if (userPermittedSpaceIds.isEmpty()) {
            return null;
        }

        // Filter Text
        Query filterTextQuery;
        if (query.getFilterText() != null && !query.getFilterText().trim().isEmpty() && !query.getFilterText().trim().equals("*")) {
            filterTextQuery = queryBuilder.keyword()
                    .onFields(Arrays.stream(IndexedDocumentModel.FILTER_TEXT_FIELDS).map(fieldMapper).toArray(String[]::new))
                    .matching(query.getFilterText())
                    .createQuery();
        } else {
            filterTextQuery = queryBuilder.all().createQuery();
        }


        // Filters
        BooleanJunction<BooleanJunction> boolQueryBuilder = queryBuilder.bool();
        for (org.clarksnut.query.Query q : query.getFilters()) {
            boolQueryBuilder.must(LuceneQueryParser.toLuceneQuery(q, new DocumentFieldMapper(), queryBuilder));
        }

        String permittedSpaceIdsString = userPermittedSpaceIds.stream().collect(Collectors.joining(" "));
        Query spaceFilterQuery = queryBuilder.bool()
                .should(queryBuilder.keyword().onField(fieldMapper.apply(IndexedDocumentModel.SUPPLIER_ASSIGNED_ID)).matching(permittedSpaceIdsString).createQuery())
                .should(queryBuilder.keyword().onField(fieldMapper.apply(IndexedDocumentModel.CUSTOMER_ASSIGNED_ID)).matching(permittedSpaceIdsString).createQuery())
                .createQuery();

        boolQueryBuilder.must(filterTextQuery);
        boolQueryBuilder.filteredBy(spaceFilterQuery);
        return boolQueryBuilder.createQuery();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public SearchResultModel<IndexedDocumentModel> getDocumentsUser(UserModel user, IndexedDocumentQueryModel query, SpaceModel... space) {
        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(em);

        QueryBuilder queryBuilder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(IndexedDocumentEntity.class).get();

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

        FullTextQuery fullTextQuery = fullTextEm.createFullTextQuery(luceneQuery, IndexedDocumentEntity.class);

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

        // Faceting
        FacetingRequest typeFacet = queryBuilder.facet()
                .name("typeFacet")
                .onField("type")
                .discrete()
                .orderedBy(FacetSortOrder.COUNT_DESC)
                .includeZeroCounts(false)
                .createFacetingRequest();
        FacetingRequest currencyFacet = queryBuilder.facet()
                .name("currencyFacet")
                .onField("currency")
                .discrete()
                .orderedBy(FacetSortOrder.COUNT_DESC)
                .includeZeroCounts(false)
                .createFacetingRequest();
        FacetingRequest amountFacet = queryBuilder.facet()
                .name("amountFacet")
                .onField("amount_face")
                .range()
                .below(1_000)
                .from(1_001).to(10_000)
                .above(10_000).excludeLimit()
                .createFacetingRequest();

        FacetManager facetManager = fullTextQuery.getFacetManager();
        facetManager.enableFaceting(typeFacet);
        facetManager.enableFaceting(currencyFacet);
        facetManager.enableFaceting(amountFacet);

        // Result
        List<IndexedDocumentEntity> resultList = fullTextQuery.getResultList();
        List<Facet> typeFacetResult = facetManager.getFacets("typeFacet");
        List<Facet> currencyFacetResult = facetManager.getFacets("currencyFacet");
        List<Facet> amountFacetResult = facetManager.getFacets("amountFacet");

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
