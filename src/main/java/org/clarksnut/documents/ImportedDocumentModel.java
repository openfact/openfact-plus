package org.clarksnut.documents;

import org.clarksnut.files.FileModel;

import java.util.Date;
import java.util.Set;

public interface ImportedDocumentModel {

    String getId();
    DocumentProviderType getProvider();

    boolean isCompressed();
    FileModel getFile();

    ImportedDocumentStatus getStatus();
    void setStatus(ImportedDocumentStatus status);

    Date getCreatedAt();
    Date getUpdatedAt();

    ImportedDocumentModel getParent();
    Set<ImportedDocumentModel> getChildren();

    String getDocumentReferenceId(String id);
    void setDocumentReferenceId(String id);
}
