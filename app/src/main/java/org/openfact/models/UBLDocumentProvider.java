package org.openfact.models;

public interface UBLDocumentProvider {

    /**
     * @param file that represents the xml document to be persisted
     */
    UBLDocumentModel addDocument(FileModel file);

}
