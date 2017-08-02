package org.openfact.models;

import org.w3c.dom.Document;

public interface DocumentProvider {

    /**
     * @param document to be persisted
     * @param file     attached to the document
     */
    DocumentModel addDocument(Document document, FileModel file);

}
