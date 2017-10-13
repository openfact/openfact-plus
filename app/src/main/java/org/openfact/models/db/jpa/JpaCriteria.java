package org.openfact.models.db.jpa;

import org.openfact.models.QueryModel;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JpaCriteria<T, R> {

    protected final EntityManager em;
    protected final QueryModel query;

    private final Class<T> tClass;
    private final Class<R> rClass;

    private final String[] searchFields;

    protected final CriteriaBuilder cb;
    protected final CriteriaQuery cq;
    protected final Root<T> root;

    protected final Set<Predicate> predicates;

    public JpaCriteria(EntityManager em, Class<T> tClass, Class<R> rClass, QueryModel query, String[] searchFields) {
        this.em = em;
        this.query = query;

        this.tClass = tClass;
        this.rClass = rClass;

        this.searchFields = searchFields;

        this.cb = em.getCriteriaBuilder();
        this.cq = cb.createQuery(tClass);
        this.root = cq.from(tClass);

        this.predicates = new HashSet<>();

        this.init();
    }

    protected void init() {
        if (query.getFilterText() != null && !query.getFilterText().trim().isEmpty()) {
            Set<Predicate> orPredicates = new HashSet<>();
            for (String field : searchFields) {
                orPredicates.add(cb.like(cb.upper(root.get(field)), "%" + query.getFilterText().toUpperCase() + "%"));
            }
            predicates.add(cb.or(orPredicates.toArray(new Predicate[orPredicates.size()])));
        }

        if (query.getFilters() != null && !query.getFilters().isEmpty()) {
            Set<Predicate> andPredicates = new HashSet<>();
            for (Map.Entry<String, String> entry : query.getFilters().entrySet()) {
                andPredicates.add(cb.like(cb.upper(root.get(entry.getKey())), "%" + entry.getValue().toUpperCase() + "%"));
            }
            predicates.add(cb.and(andPredicates.toArray(new Predicate[andPredicates.size()])));
        }
    }

    public TypedQuery<R> buildTypedQuery() {
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        }
        return em.createQuery(cq.select(root));
    }

}
