package org.clarksnut.models.jpa.config;

import org.hibernate.search.elasticsearch.analyzer.definition.ElasticsearchAnalysisDefinitionProvider;
import org.hibernate.search.elasticsearch.analyzer.definition.ElasticsearchAnalysisDefinitionRegistryBuilder;

public class ClarksnutESAnalyzerProvider implements ElasticsearchAnalysisDefinitionProvider {

    @Override
    public void register(ElasticsearchAnalysisDefinitionRegistryBuilder builder) {
        builder.tokenFilter("my_standard_filter")
                .type("standard");
        builder.tokenFilter("my_lower_case_filter")
                .type("lowercase");
        builder.tokenFilter("my_ascii_filter")
                .type("asciifolding");
        builder.tokenFilter("my_apostrophe_filter")
                .type("apostrophe");

        builder
                .analyzer("staticTextAnalyzer")
                .withTokenizer("standard")
                .withTokenFilters("my_standard_filter")
                .withTokenFilters("my_lower_case_filter")
                .withTokenFilters("my_ascii_filter");

        builder
                .analyzer("nameTextAnalyzer")
                .withTokenizer("standard")
                .withTokenFilters("my_standard_filter")
                .withTokenFilters("my_lower_case_filter")
                .withTokenFilters("my_ascii_filter")
                .withTokenFilters("my_apostrophe_filter");
    }
}
