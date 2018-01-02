package org.clarksnut.documents.jpa.config;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.analyzer.definition.LuceneAnalysisDefinitionProvider;
import org.hibernate.search.analyzer.definition.LuceneAnalysisDefinitionRegistryBuilder;

import java.util.Objects;

public class ClarksnutLuceneAnalyzerProvider implements LuceneAnalysisDefinitionProvider {

    @Override
    public void register(LuceneAnalysisDefinitionRegistryBuilder builder) {
        String indexManager = System.getenv("HIBERNATE_INDEX_MANAGER");

        if (!Objects.equals(indexManager, "elasticsearch")) {
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
