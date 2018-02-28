package org.clarksnut.models;

import org.clarksnut.mapper.document.DocumentMapped.DocumentBean;
import org.clarksnut.models.exceptions.AlreadyImportedDocumentException;

import java.util.List;

public interface DocumentProvider {

    DocumentModel addDocument(
            String documentType,
            ImportedDocumentModel importedDocument,
            DocumentBean bean) throws AlreadyImportedDocumentException;

    /**
     * @param id unique identity generated by the system
     * @return document
     */
    DocumentModel getDocument(String id);

    /**
     * @param type       document type
     * @param assignedId document assigned id
     * @return document
     */
    DocumentModel getDocument(String supplierAssignedId, String type, String assignedId);

    List<DocumentModel> getDocuments(String filterText, SpaceModel... space);

    List<DocumentModel> getDocuments(String filterText, int offset, int limit, SpaceModel... space);

    SearchResultModel<DocumentModel> searchDocuments(DocumentQueryModel query, SpaceModel... space);

    /**
     * @param document document to be removed
     * @return true if document was removed
     */
    boolean removeDocument(DocumentModel document);
}
