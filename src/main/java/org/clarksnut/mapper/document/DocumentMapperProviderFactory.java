package org.clarksnut.mapper.document;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class DocumentMapperProviderFactory {

    private static DocumentMapperProviderFactory INSTANCE;

    private Map<String, Map<String, DocumentMapperProvider>> providers = new HashMap<>();

    private DocumentMapperProviderFactory() {
        synchronized (this) {
            ServiceLoader<DocumentMapperProvider> serviceLoader = ServiceLoader.load(DocumentMapperProvider.class);
            for (DocumentMapperProvider e : serviceLoader) {
                if (!providers.containsKey(e.getGroup())) {
                    providers.put(e.getGroup(), new HashMap<>());
                }
                providers.get(e.getGroup()).put(e.getSupportedDocumentType(), e);
            }
        }
    }

    public static DocumentMapperProviderFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DocumentMapperProviderFactory();
        }
        return INSTANCE;
    }

    public DocumentMapperProvider getParsedDocumentProvider(String group, String documentType) {
        Map<String, DocumentMapperProvider> map = providers.get(group);
        if (map != null) {
            return map.get(documentType);
        }
        return null;
    }

}
