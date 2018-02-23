package org.clarksnut.models.jpa;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.clarksnut.models.*;
import org.clarksnut.models.jpa.IndexedManagerType.Type;
import org.clarksnut.models.jpa.entity.IndexedDocumentEntity;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
@IndexedManagerType(type = Type.LUCENE)
public class LuceneIndexedDocumentProvider extends AbstractIndexedDocumentProvider implements IndexedDocumentProvider {

    private static final Logger logger = Logger.getLogger(LuceneIndexedDocumentProvider.class);

    private static final Function<Facet, FacetModel> toFacetModel = (facet) -> new FacetModel() {
        @Override
        public String getValue() {
            return facet.getValue();
        }

        @Override
        public int getCount() {
            return facet.getCount();
        }
    };

    @PersistenceContext
    private EntityManager em;

    public org.apache.lucene.search.Query getQuery(IndexedDocumentQueryModel query, QueryBuilder queryBuilder, SpaceModel... space) {
        DocumentFieldMapper fieldMapper = new DocumentFieldMapper();

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

        String spaceAssignedIds = Stream.of(space).map(SpaceModel::getAssignedId).collect(Collectors.joining(" "));
        Query spaceFilterQuery = queryBuilder.bool()
                .should(queryBuilder.keyword().onField(fieldMapper.apply(IndexedDocumentModel.SUPPLIER_ASSIGNED_ID)).matching(spaceAssignedIds).createQuery())
                .should(queryBuilder.keyword().onField(fieldMapper.apply(IndexedDocumentModel.CUSTOMER_ASSIGNED_ID)).matching(spaceAssignedIds).createQuery())
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
    public IndexedDocumentModel getIndexedDocument(String documentId) {
        return null;
    }

    @Override
    public SearchResultModel<IndexedDocumentModel> getDocumentsUser(IndexedDocumentQueryModel query, SpaceModel... space) {
        if (space == null || space.length == 0) {
            return new EmptySearchResultAdapter<>();
        }

        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(em);
        QueryBuilder queryBuilder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(IndexedDocumentEntity.class).get();
        Query luceneQuery = getQuery(query, queryBuilder, space);

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
                .onField("amountFacet")
                .range()
                .below(1_000)
                .from(1_001).to(10_000)
                .above(10_000).excludeLimit()
                .createFacetingRequest();
        FacetingRequest issueDateFacet = queryBuilder.facet()
                .name("issueDateFacet")
                .onField("issueDateFacet")
                .range()
                .below(Calendar.getInstance().getTime())
                .createFacetingRequest();

        FacetManager facetManager = fullTextQuery.getFacetManager();
        facetManager.enableFaceting(typeFacet);
        facetManager.enableFaceting(currencyFacet);
        facetManager.enableFaceting(amountFacet);
        facetManager.enableFaceting(issueDateFacet);

        // Result List
        List<IndexedDocumentEntity> resultList = fullTextQuery.getResultList();

        // Result Facet
        List<Facet> typeFacetResult = facetManager.getFacets("typeFacet");
        List<Facet> currencyFacetResult = facetManager.getFacets("currencyFacet");
        List<Facet> amountFacetResult = facetManager.getFacets("amountFacet");
        List<Facet> issueDateFacetResult = facetManager.getFacets("issueDateFacet");

        Map<String, List<FacetModel>> resultFacets = new HashMap<>();
        resultFacets.put(IndexedDocumentModel.TYPE, typeFacetResult.stream().map(toFacetModel).collect(Collectors.toList()));
        resultFacets.put(IndexedDocumentModel.CURRENCY, currencyFacetResult.stream().map(toFacetModel).collect(Collectors.toList()));
        resultFacets.put(IndexedDocumentModel.AMOUNT, amountFacetResult.stream().map(toFacetModel).collect(Collectors.toList()));
        resultFacets.put(IndexedDocumentModel.ISSUE_DATE, issueDateFacetResult.stream().map(toFacetModel).collect(Collectors.toList()));

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

            @Override
            public Map<String, List<FacetModel>> getFacets() {
                return resultFacets;
            }
        };
    }

}
