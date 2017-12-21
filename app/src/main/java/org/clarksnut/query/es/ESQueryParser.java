package org.clarksnut.query.es;

import org.clarksnut.query.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.function.Function;

public class ESQueryParser {

    private static final Logger logger = Logger.getLogger(ESQueryParser.class);

    public static QueryBuilder toQueryBuilder(Query query, Function<String, String> fieldMapper) {
        if (query instanceof MatchAllQuery) {
            return QueryBuilders.matchAllQuery();
        }
        if (query instanceof TermQuery) {
            TermQuery q = (TermQuery) query;
            return QueryBuilders.termQuery(fieldMapper.apply(q.getName()), q.getValue());
        }
        if (query instanceof TermsQuery) {
            TermsQuery q = (TermsQuery) query;
            return QueryBuilders.termsQuery(fieldMapper.apply(q.getName()), q.getValues());
        }
        if (query instanceof MultiMatchQuery) {
            MultiMatchQuery q = (MultiMatchQuery) query;
            return QueryBuilders.multiMatchQuery(q.getText(), Arrays.stream(q.getFieldNames()).map(fieldMapper).toArray(String[]::new));
        }
        if (query instanceof RangeQuery) {
            RangeQuery q = (RangeQuery) query;
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(fieldMapper.apply(q.getName()));
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

            q.getMust().forEach(must -> boolQueryBuilder.must(toQueryBuilder(must, fieldMapper)));
            q.getMustNot().forEach(mustNot -> boolQueryBuilder.mustNot(toQueryBuilder(mustNot, fieldMapper)));

            q.getShould().forEach(should -> boolQueryBuilder.should(toQueryBuilder(should, fieldMapper)));
            if (q.getMinimumShouldMatch() != null) {
                boolQueryBuilder.minimumShouldMatch(q.getMinimumShouldMatch());
            }

            q.getFilter().forEach(filter -> boolQueryBuilder.filter(toQueryBuilder(filter, fieldMapper)));

            return boolQueryBuilder;
        }

        logger.error("Could not found implementation of Query:" + query.getQueryName());
        throw new IllegalStateException("Could not found implementation of Query:" + query.getQueryName());
    }

}
