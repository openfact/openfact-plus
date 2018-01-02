package org.clarksnut.files;

import javax.enterprise.util.AnnotationLiteral;

public class FileStorageProviderTypeLiteral extends AnnotationLiteral<FileStorageProviderType> implements FileStorageProviderType {

    private final String storage;

    public FileStorageProviderTypeLiteral(String storage) {
        this.storage = storage;
    }

    @Override
    public String storage() {
        return storage;
    }

}
