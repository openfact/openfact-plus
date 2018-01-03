package org.clarksnut.files;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Optional;

@Stateless
public class FileStorageProviderFactory {

    @Inject
    @ConfigurationValue("clarksnut.fileStorage.provider")
    private Optional<String> fileStorageProvider;

    @Inject
    private FileStorageProviderUtil util;

    @Produces
    public FileProvider getFileProvider() {
        return util.getDatasourceProvider(fileStorageProvider.orElse("jpa"));
    }

    @Produces
    @FileProviderName
    public String getFileProviderName() {
        return fileStorageProvider.orElse("jpa");
    }

}
