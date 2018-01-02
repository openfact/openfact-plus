package org.clarksnut.documents.jpa.entity;

import org.clarksnut.documents.parser.SkeletonDocument;

public class DocumentUtil {

    public static DocumentEntity toDocumentEntity(SkeletonDocument skeleton) {
        DocumentEntity entity = new DocumentEntity();

        entity.setType(skeleton.getType());
        entity.setAssignedId(skeleton.getAssignedId());
        entity.setAmount(skeleton.getAmount());
        entity.setTax(skeleton.getTax());
        entity.setCurrency(skeleton.getCurrency());
        entity.setIssueDate(skeleton.getIssueDate());

        entity.setSupplierName(skeleton.getSupplierName());
        entity.setSupplierAssignedId(skeleton.getSupplierAssignedId());
        entity.setSupplierStreetAddress(skeleton.getSupplierStreetAddress());
        entity.setSupplierCity(skeleton.getSupplierCity());
        entity.setSupplierCountry(skeleton.getSupplierCountry());

        entity.setCustomerName(skeleton.getCustomerName());
        entity.setCustomerAssignedId(skeleton.getCustomerAssignedId());
        entity.setCustomerStreetAddress(skeleton.getCustomerStreetAddress());
        entity.setCustomerCity(skeleton.getCustomerCity());
        entity.setCustomerCountry(skeleton.getCustomerCountry());

        return entity;
    }

    public static DocumentVersionEntity toDocumentVersionEntity(SkeletonDocument skeleton) {
        DocumentVersionEntity entity = new DocumentVersionEntity();

        entity.setType(skeleton.getType());
        entity.setAssignedId(skeleton.getAssignedId());
        entity.setAmount(skeleton.getAmount());
        entity.setTax(skeleton.getTax());
        entity.setCurrency(skeleton.getCurrency());
        entity.setIssueDate(skeleton.getIssueDate());

        entity.setSupplierName(skeleton.getSupplierName());
        entity.setSupplierAssignedId(skeleton.getSupplierAssignedId());
        entity.setSupplierStreetAddress(skeleton.getSupplierStreetAddress());
        entity.setSupplierCity(skeleton.getSupplierCity());
        entity.setSupplierCountry(skeleton.getSupplierCountry());

        entity.setCustomerName(skeleton.getCustomerName());
        entity.setCustomerAssignedId(skeleton.getCustomerAssignedId());
        entity.setCustomerStreetAddress(skeleton.getCustomerStreetAddress());
        entity.setCustomerCity(skeleton.getCustomerCity());
        entity.setCustomerCountry(skeleton.getCustomerCountry());

        return entity;
    }
}
