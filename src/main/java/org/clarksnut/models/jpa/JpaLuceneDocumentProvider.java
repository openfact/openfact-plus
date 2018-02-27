package org.clarksnut.models.jpa;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.clarksnut.models.*;
import org.clarksnut.models.jpa.entity.DocumentEntity;
import org.clarksnut.models.jpa.entity.PartyEntity;
import org.clarksnut.models.utils.ClarksnutModelUtils;
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
@IndexedManagerType(type = IndexedManagerType.Type.LUCENE)
public class JpaLuceneDocumentProvider extends JpaAbstractDocumentProvider implements DocumentProvider {

    private static final Logger logger = Logger.getLogger(JpaLuceneDocumentProvider.class);

    @PersistenceContext
    private EntityManager em;

    public org.apache.lucene.search.Query getQuery(DocumentQueryModel query, QueryBuilder queryBuilder, SpaceModel... space) {
        DocumentFieldMapper fieldMapper = new DocumentFieldMapper();

        // Filter Text
        Query filterTextQuery;
        if (query.getFilterText() != null && !query.getFilterText().trim().isEmpty() && !query.getFilterText().trim().equals("*")) {
            filterTextQuery = queryBuilder.keyword()
                    .onFields(Arrays.stream(DocumentModel.FILTER_TEXT_FIELDS).map(fieldMapper).toArray(String[]::new))
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
                .should(queryBuilder.keyword().onField(fieldMapper.apply(DocumentModel.SUPPLIER_ASSIGNED_ID)).matching(spaceAssignedIds).createQuery())
                .should(queryBuilder.keyword().onField(fieldMapper.apply(DocumentModel.CUSTOMER_ASSIGNED_ID)).matching(spaceAssignedIds).createQuery())
                .createQuery();

        boolQueryBuilder.must(filterTextQuery);
        boolQueryBuilder.filteredBy(spaceFilterQuery);
        return boolQueryBuilder.createQuery();
    }

    public org.apache.lucene.search.Query getQuery(String filterText, QueryBuilder queryBuilder, SpaceModel... space) {
        // Filter Text
        Query filterTextQuery = queryBuilder
                .phrase()
                .withSlop(2)
                .onField("assignedId").boostedTo(5)
                .andField("nGramAssignedId")
                .andField("edgeNGramAssignedId")
                .sentence(filterText.toLowerCase())
                .createQuery();


        // Filters
        BooleanJunction<BooleanJunction> boolQueryBuilder = queryBuilder.bool();

        String spaceAssignedIds = Stream.of(space).map(SpaceModel::getAssignedId).collect(Collectors.joining(" "));
        Query spaceFilterQuery = queryBuilder.bool()
                .should(queryBuilder.keyword().onField("supplierAssignedId").matching(spaceAssignedIds).createQuery())
                .should(queryBuilder.keyword().onField("customerAssignedId").matching(spaceAssignedIds).createQuery())
                .createQuery();

        boolQueryBuilder.must(filterTextQuery);
        boolQueryBuilder.filteredBy(spaceFilterQuery);
        return boolQueryBuilder.createQuery();
    }

    @Override
    public List<DocumentModel> getDocuments(String filterText, SpaceModel... space) {
        return getDocuments(filterText, -1, -1, space);
    }

    @Override
    public List<DocumentModel> getDocuments(String filterText, int offset, int limit, SpaceModel... space) {
        if (space == null || space.length == 0) {
            return Collections.emptyList();
        }

        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(em);
        QueryBuilder queryBuilder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(DocumentEntity.class).get();

        Query query = getQuery(filterText, queryBuilder, space);

        FullTextQuery fullTextQuery = fullTextEm.createFullTextQuery(query, DocumentEntity.class);
        if (offset != -1) fullTextQuery.setFirstResult(offset);
        if (limit != -1) fullTextQuery.setMaxResults(limit);

        List<DocumentEntity> results = fullTextQuery.getResultList();
        return results.stream()
                .map(f -> new DocumentAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public SearchResultModel<DocumentModel> searchDocuments(DocumentQueryModel query, SpaceModel... space) {
        if (space == null || space.length == 0) {
            return new EmptySearchResultAdapter<>();
        }

        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(em);
        QueryBuilder queryBuilder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(DocumentEntity.class).get();
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

        FullTextQuery fullTextQuery = fullTextEm.createFullTextQuery(luceneQuery, DocumentEntity.class);

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
                .from(ClarksnutModelUtils.getFirstDateOfPlusNMonth(-2))
                .to(ClarksnutModelUtils.getLastDateOfPlusNMonth(-2))
                .from(ClarksnutModelUtils.getFirstDateOfPlusNMonth(-1))
                .to(ClarksnutModelUtils.getLastDateOfPlusNMonth(-1))
                .above(ClarksnutModelUtils.getFirstDateOfPlusNMonth(0))
                .createFacetingRequest();

        FacetManager facetManager = fullTextQuery.getFacetManager();
        facetManager.enableFaceting(typeFacet);
        facetManager.enableFaceting(currencyFacet);
        facetManager.enableFaceting(amountFacet);
        facetManager.enableFaceting(issueDateFacet);

        // Result List
        List<DocumentEntity> resultList = fullTextQuery.getResultList();

        // Result Facet
        List<Facet> typeFacetResult = facetManager.getFacets("typeFacet");
        List<Facet> currencyFacetResult = facetManager.getFacets("currencyFacet");
        List<Facet> amountFacetResult = facetManager.getFacets("amountFacet");
        List<Facet> issueDateFacetResult = facetManager.getFacets("issueDateFacet");

        Map<String, List<FacetModel>> resultFacets = new HashMap<>();
        resultFacets.put(DocumentModel.TYPE, typeFacetResult.stream().map(DiscreteFacetAdapter::new).collect(Collectors.toList()));
        resultFacets.put(DocumentModel.CURRENCY, currencyFacetResult.stream().map(DiscreteFacetAdapter::new).collect(Collectors.toList()));
        resultFacets.put(DocumentModel.AMOUNT, amountFacetResult.stream().map(NumericRangeFacetAdapter::new).collect(Collectors.toList()));
        resultFacets.put(DocumentModel.ISSUE_DATE, issueDateFacetResult.stream().map(DateRangeFacetAdapter::new).collect(Collectors.toList()));

        List<DocumentModel> items = resultList.stream()
                .map(f -> new DocumentAdapter(em, f))
                .collect(Collectors.toList());

        return new SearchResultModel<DocumentModel>() {
            @Override
            public List<DocumentModel> getItems() {
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
