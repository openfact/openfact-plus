package org.clarksnut.documents.jpa;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentUserQueryModel;
import org.clarksnut.query.*;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class ESDocumentUserQueryParser {

    private static final Logger logger = Logger.getLogger(ESDocumentUserQueryParser.class);

    public String getQuery(UserModel user, DocumentUserQueryModel query, SpaceModel... space) {
        if (query.getDocumentFilters().isEmpty() && query.getUserDocumentFilters().isEmpty()) {
            throw new IllegalStateException("Invalid query, at least one query should be requested");
        }

        // Space query
        Set<String> userPermittedSpaceIds = getUserPermittedSpaces(user, space);
        if (userPermittedSpaceIds.isEmpty()) {
            return null;
        }

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (SimpleQuery q : query.getDocumentFilters()) {
            if (query.getDocumentFilters().isEmpty() || query.getUserDocumentFilters().isEmpty()) {
                boolQueryBuilder.filter(toQueryBuilder(q));
            } else {
                boolQueryBuilder.filter(toQueryBuilder(q, "document"));
            }
        }
        for (SimpleQuery q : query.getUserDocumentFilters()) {
            boolQueryBuilder.filter(toQueryBuilder(q));
        }

        boolQueryBuilder.should(QueryBuilders.termsQuery(SearchDocumentUserFields.toDocumentSearchField(DocumentModel.SUPPLIER_ASSIGNED_ID), userPermittedSpaceIds));
        boolQueryBuilder.should(QueryBuilders.termsQuery(SearchDocumentUserFields.toDocumentSearchField(DocumentModel.CUSTOMER_ASSIGNED_ID), userPermittedSpaceIds));
        boolQueryBuilder.minimumShouldMatch(1);
        return boolQueryBuilder.toString();
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

    private static QueryBuilder toQueryBuilder(Query query, String... prefix) {
        if (query instanceof MatchAllQuery) {
            return QueryBuilders.matchAllQuery();
        }
        if (query instanceof TermQuery) {
            TermQuery q = (TermQuery) query;
            return QueryBuilders.termQuery(SearchDocumentUserFields.toDocumentSearchField(q.getName(), prefix), q.getValue());
        }
        if (query instanceof TermsQuery) {
            TermsQuery q = (TermsQuery) query;
            return QueryBuilders.termsQuery(SearchDocumentUserFields.toDocumentSearchField(q.getName(), prefix), q.getValues());
        }
        if (query instanceof MultiMatchQuery) {
            MultiMatchQuery q = (MultiMatchQuery) query;
            return QueryBuilders.multiMatchQuery(q.getText(), Arrays.stream(q.getFieldNames()).map(name -> SearchDocumentUserFields.toDocumentSearchField(name, prefix)).toArray(String[]::new));
        }
        if (query instanceof RangeQuery) {
            RangeQuery q = (RangeQuery) query;
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(SearchDocumentUserFields.toDocumentSearchField(q.getName(), prefix));
            if (q.getFrom() != null) {
                if (q.isIncludeFrom()) {
                    rangeQueryBuilder.gte(q.getFrom());
                } else {
                    rangeQueryBuilder.gt(q.getFrom());
                }
            }
            if (q.getTo() != null) {
                if (q.isIncludeTo()) {
                    rangeQueryBuilder.lte(q.getTo());
                } else {
                    rangeQueryBuilder.lt(q.getTo());
                }
            }
            return rangeQueryBuilder;
        }
        if (query instanceof BoolQuery) {
            BoolQuery q = (BoolQuery) query;
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            q.getMust().forEach(must -> boolQueryBuilder.must(toQueryBuilder(must)));
            q.getMustNot().forEach(mustNot -> boolQueryBuilder.mustNot(toQueryBuilder(mustNot)));

            q.getShould().forEach(should -> boolQueryBuilder.should(toQueryBuilder(should)));
            if (q.getMinimumShouldMatch() != null) {
                boolQueryBuilder.minimumShouldMatch(q.getMinimumShouldMatch());
            }

            q.getFilter().forEach(filter -> boolQueryBuilder.filter(toQueryBuilder(filter)));

            return boolQueryBuilder;
        }

        logger.error("Could not found implementation of Query:" + query.getQueryName());
        throw new IllegalStateException("Could not found implementation of Query:" + query.getQueryName());
    }

}
