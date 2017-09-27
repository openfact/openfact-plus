package org.openfact.models;

import org.w3c.dom.Document;

public interface DocumentProvider {

    /**
     * @param file that represents the xml document to be persisted
     */
    DocumentModel addDocument(FileModel file);

    DocumentModel getDocument(String documentId);

    boolean removeDocument(DocumentModel ublDocument);

    boolean isSupported(String documentType);

    boolean isSupported(byte[] bytes);

    boolean isSupported(Document document);
}
