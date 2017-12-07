package org.clarksnut.documents.es;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.analyzer.definition.LuceneAnalysisDefinitionProvider;
import org.hibernate.search.analyzer.definition.LuceneAnalysisDefinitionRegistryBuilder;
import org.hibernate.search.elasticsearch.analyzer.ElasticsearchTokenFilterFactory;
import org.hibernate.search.elasticsearch.analyzer.ElasticsearchTokenizerFactory;

import java.util.Objects;

public class ClarksnutAnalyzerProvider implements LuceneAnalysisDefinitionProvider {

    @Override
    public void register(LuceneAnalysisDefinitionRegistryBuilder builder) {
        String indexManager = System.getenv("HIBERNATE_INDEX_MANAGER");

        if (Objects.equals(indexManager, "elasticsearch")) {
            // Elasticsearch
            builder
                    .analyzer("staticTextAnalyzer")
                    .tokenizer(ElasticsearchTokenizerFactory.class)
                    .param("type", "'standard'")
                    .tokenFilter(ElasticsearchTokenFilterFactory.class)
                    .param("type", "'standard'")
                    .tokenFilter(ElasticsearchTokenFilterFactory.class)
                    .param("type", "'lowercase'")
                    .tokenFilter(ElasticsearchTokenFilterFactory.class)
                    .param("type", "'asciifolding'");

            builder
                    .analyzer("nameTextAnalyzer")
                    .tokenizer(ElasticsearchTokenizerFactory.class)
                    .param("type", "'standard'")
                    .tokenFilter(ElasticsearchTokenFilterFactory.class)
                    .param("type", "'standard'")
                    .tokenFilter(ElasticsearchTokenFilterFactory.class)
                    .param("type", "'lowercase'")
                    .tokenFilter(ElasticsearchTokenFilterFactory.class)
                    .param("type", "'asciifolding'")
                    .tokenFilter(ElasticsearchTokenFilterFactory.class)
                    .param("type", "'apostrophe'");
        } else {
            // Lucene
            builder
                    .analyzer("staticTextAnalyzer")
                    .tokenizer(StandardTokenizerFactory.class)
                    .tokenFilter(StandardFilterFactory.class)
                    .tokenFilter(LowerCaseFilterFactory.class)
                    .tokenFilter(ASCIIFoldingFilterFactory.class);

            builder
                    .analyzer("nameTextAnalyzer")
                    .tokenizer(StandardTokenizerFactory.class)
                    .tokenFilter(StandardFilterFactory.class)
                    .tokenFilter(LowerCaseFilterFactory.class)
                    .tokenFilter(ASCIIFoldingFilterFactory.class);
        }
    }
}
