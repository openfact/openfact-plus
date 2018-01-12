package org.clarksnut.documents.jpa.config;

import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.jpa.entity.DocumentUserEntity;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Resolution;
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
//        documentUserEntityMapping(mapping, isElasticsearch);

        return mapping;
    }

    private void documentEntityMapping(SearchMapping mapping, boolean isElasticsearch) {
        IndexedMapping indexedMapping = mapping.entity(DocumentEntity.class).indexed();

        indexedMapping
                .property("id", ElementType.FIELD).documentId().name("id")

                /*
                 * Basic attributes */
                .property("type", ElementType.FIELD).field().name("type").analyzer("staticTextAnalyzer")
                .property("currency", ElementType.FIELD).field().name("currency").analyzer("staticTextAnalyzer")
                .property("provider", ElementType.FIELD).field().name("provider").analyzer("staticTextAnalyzer")

                /*
                 * Supplier */
                .property("supplierName", ElementType.FIELD).field().name("supplierName").analyzer("nameTextAnalyzer")
                .property("supplierAssignedId", ElementType.FIELD).field().name("supplierAssignedId")

                /*
                 * Customer */
                .property("customerName", ElementType.FIELD).field().name("customerName").analyzer("nameTextAnalyzer")
                .property("customerAssignedId", ElementType.FIELD).field().name("customerAssignedId")

                /*
                 * Additional information */
                .property("assignedId", ElementType.FIELD).field().name("assignedId").analyze(Analyze.NO).sortableField()
                .property("amount", ElementType.FIELD).field().name("amount").analyze(Analyze.NO).sortableField()
                .property("issueDate", ElementType.FIELD).field().name("issueDate").analyze(Analyze.NO).dateBridge(Resolution.MILLISECOND)

                /*
                * Relationships */
                .property("documentUsers", ElementType.FIELD).containedIn();
    }

    private void documentUserEntityMapping(SearchMapping mapping, boolean isElasticsearch) {
        mapping
                .entity(DocumentUserEntity.class).indexed()
                .property("id", ElementType.FIELD).documentId()
                .property("starred", ElementType.FIELD).field()
                .property("viewed", ElementType.FIELD).field()
                .property("checked", ElementType.FIELD).field()
                .property("userId", ElementType.FIELD).field()
                .property("tags", ElementType.FIELD).indexEmbedded()
                .property("document", ElementType.FIELD).indexEmbedded().includeEmbeddedObjectId(true);
    }

}
