package org.clarksnut.models;

import org.clarksnut.files.FileModel;

import java.util.Date;

public interface ImportedDocumentModel {

    String getId();
    DocumentProviderType getProvider();
    FileModel getFile();

    ImportedDocumentStatus getStatus();
    void setStatus(ImportedDocumentStatus status);

    Date getCreatedAt();
    Date getUpdatedAt();
}
