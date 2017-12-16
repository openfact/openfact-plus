package org.clarksnut.documents.jpa;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.query.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.RangeMatchingContext;
import org.jboss.logging.Logger;

import java.util.Arrays;

public class QueryUtil {

    private static final Logger logger = Logger.getLogger(QueryUtil.class);

    public static QueryBuilder toESQueryBuilder(Query query) {
        if (query instanceof MatchAllQuery) {
            return QueryBuilders.matchAllQuery();
        }
        if (query instanceof TermQuery) {
            TermQuery q = (TermQuery) query;
            return QueryBuilders.termQuery(toJpaName(q.getName()), q.getValue());
        }
        if (query instanceof TermsQuery) {
            TermsQuery q = (TermsQuery) query;
            return QueryBuilders.termsQuery(toJpaName(q.getName()), q.getValues());
        }
        if (query instanceof MultiMatchQuery) {
            MultiMatchQuery q = (MultiMatchQuery) query;
            return QueryBuilders.multiMatchQuery(q.getText(), Arrays.stream(q.getFieldNames()).map(QueryUtil::toJpaName).toArray(String[]::new));
        }
        if (query instanceof RangeQuery) {
            RangeQuery q = (RangeQuery) query;
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(toJpaName(q.getName()));
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

            q.getMust().forEach(must -> boolQueryBuilder.must(toESQueryBuilder(must)));
            q.getMustNot().forEach(mustNot -> boolQueryBuilder.mustNot(toESQueryBuilder(mustNot)));

            q.getShould().forEach(should -> boolQueryBuilder.should(toESQueryBuilder(should)));
            if (q.getMinimumShouldMatch() != null) {
                boolQueryBuilder.minimumShouldMatch(q.getMinimumShouldMatch());
            }

            q.getFilter().forEach(filter -> boolQueryBuilder.filter(toESQueryBuilder(filter)));

            return boolQueryBuilder;
        }

        logger.error("Could not found implementation of Query:" + query.getQueryName());
        return null;
    }

    public static org.apache.lucene.search.Query toLuceneQuery(Query query, org.hibernate.search.query.dsl.QueryBuilder queryBuilder) {
        if (query instanceof MatchAllQuery) {
            return queryBuilder.all().createQuery();
        }
        if (query instanceof TermQuery) {
            TermQuery q = (TermQuery) query;
            return queryBuilder.keyword()
                    .onField(toJpaName(q.getName()))
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
                    .onField(toJpaName(q.getName()))
                    .matching(matching)
                    .createQuery();
        }
        if (query instanceof MultiMatchQuery) {
            MultiMatchQuery q = (MultiMatchQuery) query;
            return queryBuilder.keyword()
                    .onFields(Arrays.stream(q.getFieldNames()).map(QueryUtil::toJpaName).toArray(String[]::new))
                    .matching(q.getText())
                    .createQuery();
        }
        if (query instanceof RangeQuery) {
            RangeQuery q = (RangeQuery) query;
            RangeMatchingContext rangeMatchingContext = queryBuilder
                    .range()
                    .onField(toJpaName(q.getName()));

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

    public static String toJpaName(String name) {
        if (DocumentModel.TYPE.equals(name)) {
            return "type";
        }
        if (DocumentModel.ASSIGNED_ID.equals(name)) {
            return "assigned_id";
        }
        if (DocumentModel.CURRENCY.equals(name)) {
            return "currency";
        }
        if (DocumentModel.ISSUE_DATE.equals(name)) {
            return "issue_date";
        }
        if (DocumentModel.AMOUNT.equals(name)) {
            return "amount";
        }

        if (DocumentModel.SUPPLIER_NAME.equals(name)) {
            return "supplier_name";
        }
        if (DocumentModel.SUPPLIER_ASSIGNED_ID.equals(name)) {
            return "supplier_assigned_id";
        }

        if (DocumentModel.CUSTOMER_ASSIGNED_ID.equals(name)) {
            return "customer_assigned_id";
        }
        if (DocumentModel.CUSTOMER_NAME.equals(name)) {
            return "customer_name";
        }

        return name;
    }

}
