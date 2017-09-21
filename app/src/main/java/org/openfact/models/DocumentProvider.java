package org.openfact.models;

public interface DocumentProvider {

    /**
     * @param file that represents the xml document to be persisted
     */
    DocumentModel addDocument(FileModel file);

    DocumentModel getDocument(String documentId);

    boolean removeDocument(DocumentModel ublDocument);
}
