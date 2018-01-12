package org.clarksnut.files.uncompress;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class UncompressFileProviderFactory {

    private static UncompressFileProviderFactory INSTANCE;

    private Map<String, UncompressFileProvider> providers = new HashMap<>();

    private UncompressFileProviderFactory() {
        synchronized (this) {
            ServiceLoader<UncompressFileProvider> serviceLoader = ServiceLoader.load(UncompressFileProvider.class);
            for (UncompressFileProvider e : serviceLoader) {
                providers.put(e.getFileExtensionSupported(), e);
            }
        }
    }

    public static UncompressFileProviderFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UncompressFileProviderFactory();
        }
        return INSTANCE;
    }

    public UncompressFileProvider getUncompressFileProvider(String fileExtension) {
        UncompressFileProvider provider = providers.get(fileExtension);
        if (provider == null) {
            return providers.get("*");
        }
        return provider;
    }

}
