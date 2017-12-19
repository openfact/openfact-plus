package org.clarksnut.documents.jpa;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentUserQueryModel;
import org.clarksnut.query.*;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.RangeMatchingContext;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class LuceneDocumentUserQueryParser {

    private static final Logger logger = Logger.getLogger(LuceneDocumentUserQueryParser.class);

    public org.apache.lucene.search.Query getQuery(UserModel user, DocumentUserQueryModel query, QueryBuilder queryBuilder, SpaceModel... space) {
        if (query.getDocumentFilters().isEmpty() && query.getUserDocumentFilters().isEmpty()) {
            throw new IllegalStateException("Invalid query, at least one query should be requested");
        }

        // Space query
        Set<String> userPermittedSpaceIds = getUserPermittedSpaces(user, space);
        if (userPermittedSpaceIds.isEmpty()) {
            return null;
        }

        BooleanJunction<BooleanJunction> boolQueryBuilder = queryBuilder.bool();
        for (SimpleQuery q : query.getDocumentFilters()) {
            if (query.getDocumentFilters().isEmpty() || query.getUserDocumentFilters().isEmpty()) {
                boolQueryBuilder.must(toLuceneQuery(q, queryBuilder));
            } else {
                boolQueryBuilder.must(toLuceneQuery(q, queryBuilder, "document"));
            }
        }
        for (SimpleQuery q : query.getUserDocumentFilters()) {
            boolQueryBuilder.must(toLuceneQuery(q, queryBuilder));
        }

        String permittedSpaceIdsString = userPermittedSpaceIds.stream().collect(Collectors.joining(" "));
        boolQueryBuilder.should(queryBuilder.keyword().onField(SearchDocumentUserFields.toDocumentSearchField(DocumentModel.SUPPLIER_ASSIGNED_ID)).matching(permittedSpaceIdsString).createQuery());
        boolQueryBuilder.should(queryBuilder.keyword().onField(SearchDocumentUserFields.toDocumentSearchField(DocumentModel.CUSTOMER_ASSIGNED_ID)).matching(permittedSpaceIdsString).createQuery());
        return boolQueryBuilder.createQuery();
    }

    private Set<String> getUserPermittedSpaces(UserModel user, SpaceModel... space) {
        Set<String> allPermittedSpaceIds = user.getAllPermitedSpaces().stream()
                .map(SpaceModel::getAssignedId)
                .collect(Collectors.toSet());

        if (space != null && space.length > 0) {
            allPermittedSpaceIds.retainAll(Arrays.stream(space).map(SpaceModel::getAssignedId).collect(Collectors.toList()));
        }

        return allPermittedSpaceIds;
    }

    public static org.apache.lucene.search.Query toLuceneQuery(Query query, org.hibernate.search.query.dsl.QueryBuilder queryBuilder, String... prefix) {
        if (query instanceof MatchAllQuery) {
            return queryBuilder.all().createQuery();
        }
        if (query instanceof TermQuery) {
            TermQuery q = (TermQuery) query;
            return queryBuilder.keyword()
                    .onField(SearchDocumentUserFields.toDocumentSearchField(q.getName(), prefix))
                    .matching(q.getValue())
                    .createQuery();
        }
        if (query instanceof TermsQuery) {
            TermsQuery q = (TermsQuery) query;
            String matching = q.getValues().stream()
                    .map(String::valueOf)
                    .reduce((t, u) -> t + " | " + u)
                    .get();
            return queryBuilder.keyword()
                    .onField(SearchDocumentUserFields.toDocumentSearchField(q.getName(), prefix))
                    .matching(matching)
                    .createQuery();
        }
        if (query instanceof MultiMatchQuery) {
            MultiMatchQuery q = (MultiMatchQuery) query;
            return queryBuilder.keyword()
                    .onFields(Arrays.stream(q.getFieldNames()).map(name -> SearchDocumentUserFields.toDocumentSearchField(name, prefix)).toArray(String[]::new))
                    .matching(q.getText())
                    .createQuery();
        }
        if (query instanceof RangeQuery) {
            RangeQuery q = (RangeQuery) query;
            RangeMatchingContext rangeMatchingContext = queryBuilder
                    .range()
                    .onField(SearchDocumentUserFields.toDocumentSearchField(q.getName(), prefix));

            if (q.getFrom() != null && q.getTo() != null) {
                return rangeMatchingContext
                        .from(q.getFrom())
                        .to(q.getTo())
                        .createQuery();
            }

            if (q.getFrom() != null) {
                return rangeMatchingContext
                        .above(q.getFrom())
                        .createQuery();
            }

            if (q.getTo() != null) {
                return rangeMatchingContext
                        .below(q.getTo())
                        .createQuery();
            }
        }
        if (query instanceof BoolQuery) {
            BoolQuery q = (BoolQuery) query;
            BooleanJunction<BooleanJunction> boolQueryBuilder = queryBuilder.bool();

            q.getMust().forEach(must -> boolQueryBuilder.must(toLuceneQuery(must, queryBuilder)));
            q.getMustNot().forEach(mustNot -> boolQueryBuilder.must(toLuceneQuery(mustNot, queryBuilder)).not());

            q.getShould().forEach(should -> boolQueryBuilder.should(toLuceneQuery(should, queryBuilder)));

            q.getFilter().forEach(filter -> boolQueryBuilder.filteredBy(toLuceneQuery(filter, queryBuilder)));

            return boolQueryBuilder.createQuery();
        }

        logger.error("Could not found implementation of Query:" + query.getQueryName());
        return null;
    }


}
