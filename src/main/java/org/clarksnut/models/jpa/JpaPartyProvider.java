package org.clarksnut.models.jpa;

import org.apache.lucene.search.Query;
import org.clarksnut.models.PartyModel;
import org.clarksnut.models.PartyProvider;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.jpa.entity.PartyEntity;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
public class JpaPartyProvider implements PartyProvider {

    public static final String[] AUTOCOMPLETE_FIELDS = {
            "nGramName", "edgeNGramName",
            "nGramPartyAssignedId", "edgeNGramAssignedId",
            "nGramPartyNames", "edgeNGramPartyNames"
    };

    @PersistenceContext
    private EntityManager em;

    public org.apache.lucene.search.Query getQuery(String filterText, QueryBuilder queryBuilder, SpaceModel... space) {
        // Filter Text
        Query filterTextQuery = queryBuilder
                .phrase()
                .withSlop(2)
                .onField(AUTOCOMPLETE_FIELDS[0])
                .andField(AUTOCOMPLETE_FIELDS[1])
                .andField(AUTOCOMPLETE_FIELDS[2])
                .andField(AUTOCOMPLETE_FIELDS[3])
                .andField(AUTOCOMPLETE_FIELDS[4])
                .andField(AUTOCOMPLETE_FIELDS[5])
                .sentence(filterText.toLowerCase())
                .createQuery();


        // Filters
        BooleanJunction<BooleanJunction> boolQueryBuilder = queryBuilder.bool();

        String spaceAssignedIds = Stream.of(space).map(SpaceModel::getAssignedId).collect(Collectors.joining(" "));
        Query spaceFilterQuery = queryBuilder.bool()
                .should(queryBuilder.keyword().onField("supplierCustomerAssignedId").matching(spaceAssignedIds).createQuery())
                .should(queryBuilder.keyword().onField("supplierCustomerAssignedId").matching(spaceAssignedIds).createQuery())
                .createQuery();

        boolQueryBuilder.must(filterTextQuery);
        boolQueryBuilder.filteredBy(spaceFilterQuery);
        return boolQueryBuilder.createQuery();
    }

    @Override
    public PartyModel getPartyByAssignedId(String assignedId, String supplierCustomerAssignedId) {
        TypedQuery<PartyEntity> query = em.createNamedQuery("getIndexedPartyByAssignedIdAndSupplierCustomerAssignedId", PartyEntity.class);
        query.setParameter("assignedId", assignedId);
        query.setParameter("supplierCustomerAssignedId", supplierCustomerAssignedId);
        List<PartyEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new IndexedPartyAdapter(em, entities.get(0));
    }

    @Override
    public List<PartyModel> getParties(String filterText, int limit, SpaceModel... space) {
        if (space == null || space.length == 0) {
            return Collections.emptyList();
        }

        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(em);
        QueryBuilder queryBuilder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(PartyEntity.class).get();

        Query query = getQuery(filterText, queryBuilder, space);

        FullTextQuery fullTextQuery = fullTextEm.createFullTextQuery(query, PartyEntity.class);
        fullTextQuery.setMaxResults(limit);

        List<PartyEntity> results = fullTextQuery.getResultList();
        return results.stream()
                .map(f -> new IndexedPartyAdapter(em, f))
                .collect(Collectors.toList());
    }
}
