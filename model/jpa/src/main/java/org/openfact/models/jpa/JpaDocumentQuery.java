package org.openfact.models.jpa;

import org.openfact.models.Constants;
import org.openfact.models.DocumentModel;
import org.openfact.models.DocumentQuery;
import org.openfact.models.jpa.entities.DocumentEntity;
import org.openfact.models.search.PagingModel;
import org.openfact.models.search.SearchCriteriaFilterOperator;
import org.openfact.models.search.SearchResultsModel;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JpaDocumentQuery implements DocumentQuery {

    private DocumentCriteria<DocumentEntity, DocumentEntity> query;
    private DocumentCriteria<DocumentEntity, Long> queryCount;

    private EntityManager em;

    public JpaDocumentQuery(EntityManager em) {
        this.em = em;

        this.query = new DocumentCriteria<>(em, DocumentEntity.class, DocumentEntity.class);
        this.queryCount = new DocumentCriteria<>(em, DocumentEntity.class, Long.class);
    }

    private DocumentModel toModel(DocumentEntity entity) {
        return new DocumentAdapter(em, entity);
    }

    @Override
    public DocumentQuery filterText(String filterText) {
        return null;
    }

    @Override
    public DocumentQuery currencyCode(String... currencyCode) {
        query.currencyCode(currencyCode);
        queryCount.currencyCode(currencyCode);
        return this;
    }

    @Override
    public DocumentQuery documentType(String... documentType) {
        query.documentType(documentType);
        queryCount.documentType(documentType);
        return this;
    }

    @Override
    public SupplierQuery supplier(String assignedAccountId) {
        Predicate supplier = query.supplier(assignedAccountId);
        return new SupplierQuery() {
            @Override
            public BuildQuery andCustomer(String assignedAccountId) {
                Predicate customer = query.customer(assignedAccountId);
                query.applyAndPredicate(supplier, customer);
                return () -> JpaDocumentQuery.this;
            }

            @Override
            public BuildQuery orCustomer(String assignedAccountId) {
                Predicate customer = query.customer(assignedAccountId);
                query.applyOrPredicate(supplier, customer);
                return () -> JpaDocumentQuery.this;
            }

            @Override
            public DocumentQuery buildQuery() {
                query.applyAndPredicate(supplier);
                return JpaDocumentQuery.this;
            }
        };
    }

    @Override
    public CustomerQuery customer(String assignedAccountId) {
        Predicate customer = query.customer(assignedAccountId);
        return new CustomerQuery() {
            @Override
            public BuildQuery andSupplier(String assignedAccountId) {
                Predicate supplier = query.supplier(assignedAccountId);
                query.applyAndPredicate(customer, supplier);
                return () -> JpaDocumentQuery.this;
            }

            @Override
            public BuildQuery orSupplier(String assignedAccountId) {
                Predicate supplier = query.supplier(assignedAccountId);
                query.applyOrPredicate(customer, supplier);
                return () -> JpaDocumentQuery.this;
            }

            @Override
            public DocumentQuery buildQuery() {
                query.applyAndPredicate(customer);
                return JpaDocumentQuery.this;
            }
        };
    }

    @Override
    public DocumentQuery addFilter(String key, Object value, SearchCriteriaFilterOperator operator) {
        query.addFilter(key, value, operator);
        queryCount.addFilter(key, value, operator);
        return this;
    }

    @Override
    public DocumentQuery fromDate(Date fromDate, boolean include) {
        query.fromDate(fromDate, include);
        queryCount.fromDate(fromDate, include);
        return this;
    }

    @Override
    public DocumentQuery toDate(Date toDate, boolean include) {
        query.toDate(toDate, include);
        queryCount.toDate(toDate, include);
        return this;
    }

    @Override
    public EntityQuery entityQuery() {
        return new JpaEntityQuery();
    }

    @Override
    public CountQuery countQuery() {
        return new JpaCountQuery();
    }

    class JpaEntityQuery implements DocumentQuery.EntityQuery {

        private Map<String, Boolean> orderBy = new HashMap<>();

        @Override
        public EntityQuery orderByAsc(String... attribute) {
            for (int i = 0; i < attribute.length; i++) {
                this.orderBy.put(attribute[i], true);
            }
            return this;
        }

        @Override
        public EntityQuery orderByDesc(String... attribute) {
            for (int i = 0; i < attribute.length; i++) {
                this.orderBy.put(attribute[i], false);
            }
            return this;
        }

        @Override
        public ListEntityQuery resultList() {
            query.orderBy(orderBy);
            return new JpaListEntityQuery();
        }

        @Override
        public SearchResultEntityQuery searchResult() {
            query.orderBy(orderBy);
            return new JpaSearchResultEntityQuery();
        }

    }

    class JpaListEntityQuery implements DocumentQuery.ListEntityQuery {
        private Integer firstResult;
        private Integer maxResults;

        @Override
        public List<DocumentModel> getResultList() {
            TypedQuery<DocumentEntity> typedQuery = query.buildQuery(false);
            if (firstResult != null) {
                typedQuery.setFirstResult(firstResult);
            }
            if (maxResults != null) {
                typedQuery.setMaxResults(maxResults);
            }

            return typedQuery.getResultList().stream()
                    .map(JpaDocumentQuery.this::toModel)
                    .collect(Collectors.toList());
        }

        @Override
        public ListEntityQuery firstResult(int firstResult) {
            if (firstResult != -1) {
                this.firstResult = firstResult;
            }
            return this;
        }

        @Override
        public ListEntityQuery maxResults(int maxResults) {
            if (maxResults != -1) {
                this.maxResults = maxResults;
            }
            return this;
        }
    }

    class JpaSearchResultEntityQuery implements DocumentQuery.SearchResultEntityQuery {
        @Override
        public SearchResultsModel<DocumentModel> getSearchResult() {
            PagingModel paging = new PagingModel();
            paging.setPage(1);
            paging.setPageSize(Constants.DEFAULT_MAX_RESULTS);
            return getSearchResult(paging);
        }

        @Override
        public SearchResultsModel<DocumentModel> getSearchResult(PagingModel paging) {
            int page = paging.getPage();
            int pageSize = paging.getPageSize();
            int start = (page - 1) * pageSize;

            TypedQuery<DocumentEntity> typedQuery = query.buildQuery(false);

            typedQuery.setFirstResult(start);
            typedQuery.setMaxResults(pageSize + 1);
            boolean hasMore = false;

            List<DocumentEntity> resultList = typedQuery.getResultList();

            // Check if we got back more than we actually needed.
            if (resultList.size() > pageSize) {
                resultList.remove(resultList.size() - 1);
                hasMore = true;
            }

            // If there are more results than we needed, then we will need to do
            // another
            // createQuery to determine how many rows there are in total
            int totalSize = start + resultList.size();
            if (hasMore) {
                totalSize = countQuery().getTotalCount();
            }

            SearchResultsModel<DocumentModel> results = new SearchResultsModel<>();
            results.setTotalSize(totalSize);
            results.setModels(resultList.stream().map(JpaDocumentQuery.this::toModel).collect(Collectors.toList()));
            return results;
        }
    }

    class JpaCountQuery implements DocumentQuery.CountQuery {
        @Override
        public int getTotalCount() {
            queryCount.getCriteriaQuery().select(queryCount.getCriteriaBuilder().count(queryCount.getRoot()));
            TypedQuery<Long> query = queryCount.buildQuery(true);
            return query.getSingleResult().intValue();
        }
    }

}
