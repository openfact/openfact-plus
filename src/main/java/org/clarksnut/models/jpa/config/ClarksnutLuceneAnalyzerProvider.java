package org.clarksnut.models.jpa.config;

import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.apache.lucene.analysis.pattern.PatternReplaceFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.analyzer.definition.LuceneAnalysisDefinitionProvider;
import org.hibernate.search.analyzer.definition.LuceneAnalysisDefinitionRegistryBuilder;

public class ClarksnutLuceneAnalyzerProvider implements LuceneAnalysisDefinitionProvider {

    @Override
    public void register(LuceneAnalysisDefinitionRegistryBuilder builder) {
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


        builder
                .analyzer("standardAnalyzer")
                .tokenizer(StandardTokenizerFactory.class)
                .tokenFilter(WordDelimiterFilterFactory.class)
                .tokenFilter(LowerCaseFilterFactory.class)
                .tokenFilter(PatternReplaceFilterFactory.class)
                .param("pattern", "([^a-zA-Z0-9\\.])")
                .param("replacement", " ")
                .param("pattern", "all");
        builder
                .analyzer("autocompleteNGramAnalyzer")
                .tokenizer(StandardTokenizerFactory.class)
                .tokenFilter(WordDelimiterFilterFactory.class)
                .tokenFilter(LowerCaseFilterFactory.class)
                .tokenFilter(NGramFilterFactory.class)
                .param("minGramSize", "3")
                .param("maxGramSize", "5")
                .tokenFilter(PatternReplaceFilterFactory.class)
                .param("pattern", "([^a-zA-Z0-9\\.])")
                .param("replacement", " ")
                .param("pattern", "all");
        builder
                .analyzer("autocompleteEdgeAnalyzer")
                .tokenizer(KeywordTokenizerFactory.class)
                .tokenFilter(PatternReplaceFilterFactory.class)
                .param("pattern", "([^a-zA-Z0-9\\.])")
                .param("replacement", " ")
                .param("pattern", "all")
                .tokenFilter(LowerCaseFilterFactory.class)
                .tokenFilter(StopFilterFactory.class)
                .tokenFilter(EdgeNGramFilterFactory.class)
                .param("minGramSize", "3")
                .param("maxGramSize", "50");
    }

}
