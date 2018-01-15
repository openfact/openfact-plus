package org.clarksnut.documents.jpa.config;

import org.hibernate.search.elasticsearch.analyzer.definition.ElasticsearchAnalysisDefinitionProvider;
import org.hibernate.search.elasticsearch.analyzer.definition.ElasticsearchAnalysisDefinitionRegistryBuilder;

import java.util.Objects;

public class ClarksnutESAnalyzerProvider implements ElasticsearchAnalysisDefinitionProvider {

    @Override
    public void register(ElasticsearchAnalysisDefinitionRegistryBuilder builder) {
        String indexManager = System.getenv("HIBERNATE_INDEX_MANAGER");

        if (Objects.equals(indexManager, "elasticsearch")) {
            // Elasticsearch
            builder
                    .analyzer("staticTextAnalyzer");
//                    .withTokenizer(ElasticsearchTokenizerFactory.class)
//                    .param("type", "'standard'")
//                    .tokenFilter(ElasticsearchTokenFilterFactory.class)
//                    .param("type", "'standard'")
//                    .tokenFilter(ElasticsearchTokenFilterFactory.class)
//                    .param("type", "'lowercase'")
//                    .tokenFilter(ElasticsearchTokenFilterFactory.class)
//                    .param("type", "'asciifolding'");

            builder
                    .analyzer("nameTextAnalyzer");
//                    .tokenizer(ElasticsearchTokenizerFactory.class)
//                    .param("type", "'standard'")
//                    .tokenFilter(ElasticsearchTokenFilterFactory.class)
//                    .param("type", "'standard'")
//                    .tokenFilter(ElasticsearchTokenFilterFactory.class)
//                    .param("type", "'lowercase'")
//                    .tokenFilter(ElasticsearchTokenFilterFactory.class)
//                    .param("type", "'asciifolding'")
//                    .tokenFilter(ElasticsearchTokenFilterFactory.class)
//                    .param("type", "'apostrophe'");
        }
    }
}
