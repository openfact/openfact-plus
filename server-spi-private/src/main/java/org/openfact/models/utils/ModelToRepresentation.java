package org.openfact.models.utils;

import org.openfact.models.DocumentModel;
import org.openfact.representations.idm.DocumentRepresentation;

import javax.ejb.Stateless;
import java.util.HashMap;

@Stateless
public class ModelToRepresentation {

    public DocumentRepresentation toRepresentation(DocumentModel model) {
        DocumentRepresentation rep = new DocumentRepresentation();
        rep.setId(model.getId());
        rep.setDocumentId(model.getDocumentId());
        rep.setDocumentType(model.getDocumentType());
        rep.setSupplierAssignedAccountId(model.getSupplierAssignedAccountId());
        if (model.getDocumentCurrencyCode() != null) {
            rep.setDocumentCurrencyCode(model.getDocumentCurrencyCode());
        }
        if (model.getCustomerAssignedAccountId() != null) {
            rep.setCustomerAssignedAccountId(model.getCustomerAssignedAccountId());
        }
        rep.setAttributes(new HashMap<>(model.getAttributesFormated()));
        return rep;
    }
}
