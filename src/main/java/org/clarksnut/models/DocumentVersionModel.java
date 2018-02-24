package org.clarksnut.models;

import java.util.Date;

public interface DocumentVersionModel {

    String getId();

    boolean isCurrentVersion();

    DocumentModel getDocument();

    ImportedDocumentModel getImportedDocument();

    Date getCreatedAt();

    Date getUpdatedAt();

}
