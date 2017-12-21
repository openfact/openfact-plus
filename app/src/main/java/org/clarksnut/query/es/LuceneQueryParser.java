package org.clarksnut.query.es;

import org.clarksnut.query.*;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.RangeMatchingContext;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.function.Function;

public class LuceneQueryParser {

    private static final Logger logger = Logger.getLogger(LuceneQueryParser.class);

    public static org.apache.lucene.search.Query toLuceneQuery(Query query, Function<String, String> fieldMapper, QueryBuilder queryBuilder) {
        if (query instanceof MatchAllQuery) {
            return queryBuilder.all().createQuery();
        }
        if (query instanceof TermQuery) {
            TermQuery q = (TermQuery) query;
            return queryBuilder.keyword()
                    .onField(fieldMapper.apply(q.getName()))
                    .matching(q.getValue())
                    .createQuery();
        }
        if (query instanceof TermsQuery) {
            TermsQuery q = (TermsQuery) query;
            String matching = q.getValues().stream()
                    .map(String::valueOf)
                    .reduce((t, u) -> t + " " + u)
                    .orElse("");
            return queryBuilder.keyword()
                    .onField(fieldMapper.apply(q.getName()))
                    .matching(matching)
                    .createQuery();
        }
        if (query instanceof MultiMatchQuery) {
            MultiMatchQuery q = (MultiMatchQuery) query;
            return queryBuilder.keyword()
                    .onFields(Arrays.stream(q.getFieldNames()).map(fieldMapper).toArray(String[]::new))
                    .matching(q.getText())
                    .createQuery();
        }
        if (query instanceof RangeQuery) {
            RangeQuery q = (RangeQuery) query;
            RangeMatchingContext rangeMatchingContext = queryBuilder
                    .range()
                    .onField(fieldMapper.apply(q.getName()));

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

            q.getMust().forEach(must -> boolQueryBuilder.must(toLuceneQuery(must, fieldMapper, queryBuilder)));
            q.getMustNot().forEach(mustNot -> boolQueryBuilder.must(toLuceneQuery(mustNot, fieldMapper, queryBuilder)).not());

            q.getShould().forEach(should -> boolQueryBuilder.should(toLuceneQuery(should, fieldMapper, queryBuilder)));

            q.getFilter().forEach(filter -> boolQueryBuilder.filteredBy(toLuceneQuery(filter, fieldMapper, queryBuilder)));

            return boolQueryBuilder.createQuery();
        }

        logger.error("Could not found implementation of Query:" + query.getQueryName());
        return null;
    }


}
