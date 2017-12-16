package org.clarksnut.documents;

import org.clarksnut.documents.query.Query;

public class DocumentUserQueryModel {

    private final Query query;
    private final boolean isForUser;

    private final String orderBy;
    private final boolean asc;

    private final Integer offset;
    private final Integer limit;

    private DocumentUserQueryModel(Builder builder) {
        this.query = builder.query;
        this.isForUser = builder.isForUser;

        this.orderBy = builder.orderBy;
        this.asc = builder.asc;

        this.offset = builder.offset;
        this.limit = builder.limit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Query getQuery() {
        return query;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public boolean isAsc() {
        return asc;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public boolean isForUser() {
        return isForUser;
    }

    public static class Builder {

        private Query query;
        private boolean isForUser;

        private boolean asc;
        private String orderBy;

        private Integer offset;
        private Integer limit;

        public Builder query(Query query) {
            this.query = query;
            return this;
        }

        public Builder isForUser(boolean isForUser) {
            this.isForUser = isForUser;
            return this;
        }

        public Builder orderBy(String field, boolean asc) {
            this.orderBy = field;
            this.asc = asc;
            return this;
        }

        public Builder offset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public DocumentUserQueryModel build() {
            return new DocumentUserQueryModel(this);
        }

    }

}
