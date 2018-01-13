package org.clarksnut.documents.jpa;

import org.clarksnut.documents.IndexedDocumentProvider;
import org.clarksnut.documents.jpa.IndexedManagerType.Type;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Optional;

@Stateless
public class IndexedDocumentProviderFactory {

    @Inject
    @ConfigurationValue("clarksnut.document.index_manager")
    private Optional<String> clarksnutDocumentIndexManager;

    @Inject
    @IndexedManagerType(type = Type.ELASTICSEARCH)
    private IndexedDocumentProvider esProvider;

    @Inject
    @IndexedManagerType(type = Type.LUCENE)
    private IndexedDocumentProvider luceneProvider;

    private IndexedDocumentProvider defaultIndexedDocumentProvider;

    @PostConstruct
    public void init() {
        String indexManager = clarksnutDocumentIndexManager.orElse("lucene");
        Type indexManagerType = Type.valueOf(indexManager.toUpperCase());

        switch (indexManagerType) {
            case ELASTICSEARCH:
                defaultIndexedDocumentProvider = esProvider;
                break;
            case LUCENE:
                defaultIndexedDocumentProvider = luceneProvider;
                break;
            default:
                throw new IllegalStateException("Invalid index manager");
        }
    }

    @Produces
    public IndexedDocumentProvider getIndexedDocumentProvider() {
        return defaultIndexedDocumentProvider;
    }
}
