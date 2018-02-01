package org.clarksnut.documents.jpa.config;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.analyzer.definition.LuceneAnalysisDefinitionProvider;
import org.hibernate.search.analyzer.definition.LuceneAnalysisDefinitionRegistryBuilder;

public class ClarksnutLuceneAnalyzerProvider implements LuceneAnalysisDefinitionProvider {

    @Override
    public void register(LuceneAnalysisDefinitionRegistryBuilder builder) {
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
