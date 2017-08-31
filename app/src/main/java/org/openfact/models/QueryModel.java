package org.openfact.models;

import java.util.HashMap;
import java.util.Map;

public class QueryModel {

    private final Map<String, String> filters;

    private QueryModel(Builder builder) {
        this.filters = builder.filters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public static class Builder {

        private Map<String, String> filters = new HashMap<>();

        public Builder addFilter(String key, String value) {
            this.filters.put(key, value);
            return this;
        }

        public QueryModel build() {
            return new QueryModel(this);
        }

    }

}
