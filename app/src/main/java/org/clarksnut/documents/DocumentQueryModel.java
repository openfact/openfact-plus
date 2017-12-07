package org.clarksnut.documents;

public class DocumentQueryModel {

    private final String query;
    private final boolean isJsonQuery;

    private final String orderBy;
    private final boolean asc;

    private final Integer offset;
    private final Integer limit;

    private DocumentQueryModel(Builder builder) {
        this.query = builder.query;
        this.isJsonQuery = builder.isJsonQuery;

        this.orderBy = builder.orderBy;
        this.asc = builder.asc;

        this.offset = builder.offset;
        this.limit = builder.limit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getQuery() {
        return query;
    }

    public boolean isJsonQuery() {
        return isJsonQuery;
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

    public static class Builder {

        private String query;
        private boolean isJsonQuery;

        private String orderBy;
        private boolean asc;

        private Integer offset;
        private Integer limit;

        public Builder query(String query, boolean isJsonQuery) {
            this.query = query;
            this.isJsonQuery = isJsonQuery;
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

        public DocumentQueryModel build() {
            return new DocumentQueryModel(this);
        }

    }

}
