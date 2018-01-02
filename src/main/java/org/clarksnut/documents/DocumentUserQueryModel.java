package org.clarksnut.documents;

import org.clarksnut.query.SimpleQuery;

import java.util.HashSet;
import java.util.Set;

public class DocumentUserQueryModel {

    private final String filterText;
    private final Set<SimpleQuery> documentFilters;
    private final Set<SimpleQuery> userDocumentFilters;

    private final String orderBy;
    private final boolean asc;

    private final Integer offset;
    private final Integer limit;

    private DocumentUserQueryModel(Builder builder) {
        this.filterText = builder.filterText;
        this.documentFilters = builder.documentFilters;
        this.userDocumentFilters = builder.userDocumentFilters;

        this.orderBy = builder.orderBy;
        this.asc = builder.asc;

        this.offset = builder.offset;
        this.limit = builder.limit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getFilterText() {
        return filterText;
    }

    public Set<SimpleQuery> getDocumentFilters() {
        return documentFilters;
    }

    public Set<SimpleQuery> getUserDocumentFilters() {
        return userDocumentFilters;
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

        private String filterText;
        private Set<SimpleQuery> documentFilters = new HashSet<>();
        private Set<SimpleQuery> userDocumentFilters = new HashSet<>();

        private boolean asc;
        private String orderBy;

        private Integer offset;
        private Integer limit;

        public Builder filterText(String filterText) {
            this.filterText = filterText;
            return this;
        }

        public Builder addDocumentFilter(SimpleQuery query) {
            this.documentFilters.add(query);
            return this;
        }

        public Builder addUserDocumentFilter(SimpleQuery query) {
            this.userDocumentFilters.add(query);
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
