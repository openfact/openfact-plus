package org.openfact.models;

import java.util.List;

public interface DocumentProvider {

    DocumentQuery createQuery();

    DocumentModel addDocument(String documentType, String documentId, String supplierAssignedAccountId, String originUuid) throws ModelException;

    DocumentModel getDocumentById(String id);
    DocumentModel getDocumentByTypeIdAndSupplierAssignedAccountId(String documentType, String documentId, String supplierAssignedAccountId) throws ModelException;
    DocumentModel getDocumentByOriginUuid(String originUuid) throws ModelException;

    boolean removeDocument(String id);

    List<DocumentModel> getDocumentsBySupplier(String supplierAssignedAccountId);
    List<DocumentModel> getDocumentsBySupplier(String supplierAssignedAccountId, int firstResult, int maxResults);

    List<DocumentModel> getDocumentsByCustomer(String customerAssignedAccountId);
    List<DocumentModel> getDocumentsByCustomer(String customerAssignedAccountId, int firstResult, int maxResults);

}
