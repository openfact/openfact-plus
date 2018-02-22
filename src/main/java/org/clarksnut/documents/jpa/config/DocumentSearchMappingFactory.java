package org.clarksnut.documents.jpa.config;

import org.clarksnut.documents.jpa.entity.IndexedDocumentEntity;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.FacetEncodingType;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.cfg.SearchMapping;

import java.lang.annotation.ElementType;

public class DocumentSearchMappingFactory {

    @Factory
    public SearchMapping getSearchMapping() {
        SearchMapping mapping = new SearchMapping();
        mapping.entity(IndexedDocumentEntity.class).indexed()

                .property("id", ElementType.FIELD).documentId().name("id")

                /*
                 * Basic attributes */
                .property("type", ElementType.FIELD).field().name("type").analyze(Analyze.NO).facet()
                .property("currency", ElementType.FIELD).field().name("currency").analyze(Analyze.NO).facet()
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

                .property("amount", ElementType.FIELD)
                .field().name("amount").analyze(Analyze.NO).sortableField()
                .field().name("amount_face").analyze(Analyze.NO).facet().encoding(FacetEncodingType.LONG)

                .property("issueDate", ElementType.FIELD).field().name("issueDate").analyze(Analyze.NO).sortableField().numericField().dateBridge(Resolution.DAY)

                /*
                * Relationships */
                .property("documentUsers", ElementType.FIELD).containedIn();

        return mapping;
    }

}
