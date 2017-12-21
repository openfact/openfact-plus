package org.clarksnut.files;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.lang.annotation.Annotation;

@Stateless
public class FileStorageProviderUtil {

    @Inject
    @Any
    private Instance<FileProvider> providers;

    public FileProvider getDatasourceProvider(String storageProvider) {
        Annotation annotation = new FileStorageProviderTypeLiteral(storageProvider);
        Instance<FileProvider> instance = providers.select(annotation);
        if (instance.isAmbiguous() || instance.isUnsatisfied()) {
            return null;
        }
        return instance.get();
    }
}
