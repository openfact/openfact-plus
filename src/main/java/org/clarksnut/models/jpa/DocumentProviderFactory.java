package org.clarksnut.models.jpa;

import org.clarksnut.models.DocumentProvider;
import org.clarksnut.models.jpa.IndexedManagerType.Type;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import java.util.Optional;

@Stateless
public class DocumentProviderFactory {

    @Produces
    public DocumentProvider getIndexedDocumentProvider(@IndexedManagerType(type = Type.ELASTICSEARCH) DocumentProvider es,
                                                       @IndexedManagerType(type = Type.LUCENE) DocumentProvider lucene) {
        Optional<String> optional = Optional.ofNullable(System.getenv("CN_HIBERNATE_INDEX_MANAGER"));
        String indexManager = optional.orElse("directory-based");

        if (indexManager.equalsIgnoreCase("elasticsearch")) {
            return es;
        } else if (indexManager.equalsIgnoreCase("directory-based")) {
            return lucene;
        } else {
            throw new IllegalStateException("Invalid HIBERNATE_INDEX_MANAGER:" + indexManager);
        }
    }
}
