package org.clarksnut.models.jpa.config;

import org.clarksnut.models.jpa.entity.DocumentEntity;
import org.clarksnut.models.jpa.entity.PartyEntity;
import org.hibernate.search.annotations.*;
import org.hibernate.search.cfg.SearchMapping;

import java.lang.annotation.ElementType;

public class DocumentSearchMappingFactory {

    @Factory
    public SearchMapping getSearchMapping() {
        SearchMapping mapping = new SearchMapping();

        mapping.entity(DocumentEntity.class).indexed()
                .property("id", ElementType.FIELD).documentId().name("id")

                /*
                 * Basic attributes */
                .property("type", ElementType.FIELD).field().name("type").analyze(Analyze.NO).facet()
                .property("currency", ElementType.FIELD).field().name("currency").analyze(Analyze.NO).facet()
                .property("provider", ElementType.FIELD).field().name("provider").analyze(Analyze.NO).facet()

                /*
                 * Supplier */
                .property("supplierName", ElementType.FIELD)
                .field().name("supplierName").index(Index.YES).store(Store.YES).analyze(Analyze.YES).analyzer("standardAnalyzer")
                .field().name("nGramSupplierName").index(Index.YES).store(Store.NO).analyze(Analyze.YES).analyzer("autocompleteNGramAnalyzer")
                .field().name("edgeNGramSupplierName").index(Index.YES).store(Store.NO).analyze(Analyze.YES).analyzer("autocompleteEdgeAnalyzer")

                .property("supplierAssignedId", ElementType.FIELD)
                .field().name("supplierAssignedId").index(Index.YES).store(Store.YES).analyze(Analyze.YES).analyzer("standardAnalyzer")
                /*
                 * Customer */
                .property("customerName", ElementType.FIELD)
                .field().name("customerName").index(Index.YES).store(Store.YES).analyze(Analyze.YES).analyzer("standardAnalyzer")
                .field().name("nGramCustomerName").index(Index.YES).store(Store.NO).analyze(Analyze.YES).analyzer("autocompleteNGramAnalyzer")
                .field().name("edgeNGramCustomerName").index(Index.YES).store(Store.NO).analyze(Analyze.YES).analyzer("autocompleteEdgeAnalyzer")

                .property("customerAssignedId", ElementType.FIELD)
                .field().name("customerAssignedId").index(Index.YES).store(Store.YES).analyze(Analyze.YES).analyzer("standardAnalyzer")

                /*
                 * Additional information */
                .property("assignedId", ElementType.FIELD)
                .field().name("assignedId").analyze(Analyze.NO).sortableField()
                .field().name("nGramAssignedId").index(Index.YES).store(Store.NO).analyze(Analyze.YES).analyzer("autocompleteNGramAnalyzer")
                .field().name("edgeNGramAssignedId").index(Index.YES).store(Store.NO).analyze(Analyze.YES).analyzer("autocompleteEdgeAnalyzer")

                .property("amount", ElementType.FIELD)
                .field().name("amount").analyze(Analyze.NO).sortableField()
                .field().name("amountFacet").analyze(Analyze.NO).facet().encoding(FacetEncodingType.LONG)

                .property("issueDate", ElementType.FIELD)
                .field().name("issueDate").analyze(Analyze.NO).sortableField().numericField().dateBridge(Resolution.MILLISECOND)
                .field().name("issueDateFacet").analyze(Analyze.NO).facet().encoding(FacetEncodingType.LONG)

                /*
                 * User interactions
                 * */
                .property("userViews", ElementType.FIELD).indexEmbedded()
                .property("userStarts", ElementType.FIELD).indexEmbedded()
                .property("userChecks", ElementType.FIELD).indexEmbedded();

        mapping.entity(PartyEntity.class).indexed()
                .property("id", ElementType.FIELD).documentId().name("id")

                .property("name", ElementType.FIELD)
                .field().name("name").index(Index.YES).store(Store.YES).analyze(Analyze.YES).analyzer("standardAnalyzer")
                .field().name("nGramName").index(Index.YES).store(Store.NO).analyze(Analyze.YES).analyzer("autocompleteNGramAnalyzer")
                .field().name("edgeNGramName").index(Index.YES).store(Store.NO).analyze(Analyze.YES).analyzer("autocompleteEdgeAnalyzer")

                .property("assignedId", ElementType.FIELD)
                .field().name("assignedId").index(Index.YES).store(Store.YES).analyze(Analyze.YES).analyzer("standardAnalyzer")
                .field().name("nGramPartyAssignedId").index(Index.YES).store(Store.NO).analyze(Analyze.YES).analyzer("autocompleteNGramAnalyzer")
                .field().name("edgeNGramAssignedId").index(Index.YES).store(Store.NO).analyze(Analyze.YES).analyzer("autocompleteEdgeAnalyzer")

                .property("partyNames", ElementType.FIELD).indexEmbedded()
                .field().name("partyNames").index(Index.YES).store(Store.YES).analyze(Analyze.YES).analyzer("standardAnalyzer")
                .field().name("nGramPartyNames").index(Index.YES).store(Store.NO).analyze(Analyze.YES).analyzer("autocompleteNGramAnalyzer")
                .field().name("edgeNGramPartyNames").index(Index.YES).store(Store.NO).analyze(Analyze.YES).analyzer("autocompleteEdgeAnalyzer")

                .property("supplierCustomerAssignedId", ElementType.FIELD).field().name("supplierCustomerAssignedId");

        return mapping;
    }

}
