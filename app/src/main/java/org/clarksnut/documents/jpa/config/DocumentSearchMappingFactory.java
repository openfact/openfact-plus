package org.clarksnut.documents.jpa.config;

import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.jpa.entity.DocumentUserEntity;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.cfg.IndexedMapping;
import org.hibernate.search.cfg.SearchMapping;

import java.lang.annotation.ElementType;
import java.util.Objects;

public class DocumentSearchMappingFactory {

    @Factory
    public SearchMapping getSearchMapping() {
        String indexManager = System.getenv("HIBERNATE_INDEX_MANAGER");
        boolean isElasticsearch = !Objects.equals(indexManager, "elasticsearch");

        SearchMapping mapping = new SearchMapping();

        documentEntityMapping(mapping, isElasticsearch);
        documentUserEntityMapping(mapping, isElasticsearch);

        return mapping;
    }

    protected void documentEntityMapping(SearchMapping mapping, boolean isElasticsearch) {
        IndexedMapping indexedMapping = mapping.entity(DocumentEntity.class).indexed();

        indexedMapping
                .property("id", ElementType.METHOD).documentId()
                .property("type", ElementType.METHOD).field().analyzer("staticTextAnalyzer")
                .property("currency", ElementType.METHOD).field().analyzer("staticTextAnalyzer")
                .property("supplierName", ElementType.METHOD).field().analyzer("nameTextAnalyzer")
                .property("supplierAssignedId", ElementType.METHOD).field()
                .property("customerName", ElementType.METHOD).field().analyzer("nameTextAnalyzer")
                .property("customerAssignedId", ElementType.METHOD).field()
                .property("provider", ElementType.METHOD).field().analyzer("staticTextAnalyzer")
                .property("verified", ElementType.METHOD).field()
                .property("changed", ElementType.METHOD).field();

        if (isElasticsearch) {
            indexedMapping
                    .property("assignedId", ElementType.METHOD).field().analyze(Analyze.NO)
                    .property("issueDate", ElementType.METHOD).field().analyze(Analyze.NO)
                    .property("amount", ElementType.METHOD).field().analyze(Analyze.NO);
        } else {
            indexedMapping
                    .property("assignedId", ElementType.METHOD).field().analyze(Analyze.NO).sortableField()
                    .property("issueDate", ElementType.METHOD).field().analyze(Analyze.NO).store(Store.YES).dateBridge(Resolution.MILLISECOND)
                    .property("amount", ElementType.METHOD).field().analyze(Analyze.NO).sortableField();
        }
    }

    protected void documentUserEntityMapping(SearchMapping mapping, boolean isElasticsearch) {
        mapping
                .entity(DocumentUserEntity.class).indexed()
                .property("id", ElementType.METHOD).documentId()
                .property("starred", ElementType.METHOD).field()
                .property("viewed", ElementType.METHOD).field()
                .property("checked", ElementType.METHOD).field()
                .property("userId", ElementType.METHOD).field()
                .property("tags", ElementType.METHOD).indexEmbedded()
                .property("document", ElementType.METHOD).indexEmbedded().includeEmbeddedObjectId(true);
    }

}
