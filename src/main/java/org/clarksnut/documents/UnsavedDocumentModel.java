package org.clarksnut.documents;

public interface UnsavedDocumentModel {

    String getId();

    String getType();

    String getFileId();

    String getFileProvider();

    UnsavedReasonType getReason();

}
