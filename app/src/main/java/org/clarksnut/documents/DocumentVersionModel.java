package org.clarksnut.documents;

public interface DocumentVersionModel extends Document {

    String getId();

    boolean isCurrentVersion();

    DocumentModel getDocument();

}
