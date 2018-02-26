package org.clarksnut.models.jpa;

import org.apache.lucene.search.Query;
import org.clarksnut.models.PartyModel;
import org.clarksnut.models.PartyProvider;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.jpa.entity.PartyEntity;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class JpaPartyProvider implements PartyProvider {

    public static final String[] AUTOCOMPLETE_FIELDS = {
            "nGramName", "edgeNGramName",
            "nGramPartyAssignedId", "edgeNGramAssignedId",
            "nGramPartyNames", "edgeNGramPartyNames"
    };

    @PersistenceContext
    private EntityManager em;

    @Override
    public PartyModel getPartyByAssignedId(String assignedId) {
        TypedQuery<PartyEntity> query = em.createNamedQuery("getIndexedPartyByAssignedId", PartyEntity.class);
        query.setParameter("assignedId", assignedId);
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

        Query query = queryBuilder
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

        FullTextQuery fullTextQuery = fullTextEm.createFullTextQuery(query, PartyEntity.class);
        fullTextQuery.setMaxResults(limit);

        List<PartyEntity> results = fullTextQuery.getResultList();
        return results.stream()
                .map(f -> new IndexedPartyAdapter(em, f))
                .collect(Collectors.toList());
    }
}
