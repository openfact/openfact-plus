package org.openfact.models;

import java.util.List;

public interface DocumentProvider {

    DocumentQuery createQueryBySupplier(String supplierAssignedAccountId);
    DocumentQuery createQueryByCustomer(String customerAssignedAccountId);

    DocumentModel addDocument(String documentType, String documentId, String supplierAssignedAccountId) throws ModelException;

    DocumentModel getDocumentById(String id);

    boolean removeDocument(String id);

    List<DocumentModel> getDocumentsBySupplier(String supplierAssignedAccountId);
    List<DocumentModel> getDocumentsBySupplier(String supplierAssignedAccountId, int firstResult, int maxResults);

    List<DocumentModel> getDocumentsByCustomer(String customerAssignedAccountId);
    List<DocumentModel> getDocumentsByCustomer(String customerAssignedAccountId, int firstResult, int maxResults);

}
