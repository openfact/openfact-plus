package org.openfact.report;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportTemplateConfiguration {

    private final String themeName;
    private final Locale locale;
    private final Map<String, Object> attributes;

    private ReportTemplateConfiguration(Builder builder) {
        this.themeName = builder.themeName;
        this.attributes = builder.attributes;
        this.locale = builder.locale;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getThemeName() {
        return themeName;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Locale getLocale() {
        return locale;
    }

    public static class Builder {

        private String themeName;
        private Locale locale;
        private Map<String, Object> attributes = new HashMap<>();

        public Builder themeName(String themeName) {
            this.themeName = themeName;
            return this;
        }

        public Builder locale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Builder addAttribute(String name, Object value) {
            this.attributes.put(name, value);
            return this;
        }

        public Builder addAllAttributes(Map<String, Object> attributes) {
            this.attributes.putAll(attributes);
            return this;
        }

        public ReportTemplateConfiguration build() {
            return new ReportTemplateConfiguration(this);
        }

    }
}
