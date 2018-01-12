package org.clarksnut.documents.jpa;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentUserModel;
import org.clarksnut.documents.IndexedDocumentModel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DocumentFieldMapper implements Function<String, String> {

    public static final Map<String, String> mapper = createMap();

    private String prefix;

    public DocumentFieldMapper() {

    }

    public DocumentFieldMapper(String prefix) {
        this.prefix = prefix;
    }

    private static Map<String, String> createMap() {
        Map<String, String> map = new HashMap<>();

        // Document
        map.put(IndexedDocumentModel.TYPE, "type");
        map.put(IndexedDocumentModel.ASSIGNED_ID, "assignedId");
        map.put(IndexedDocumentModel.CURRENCY, "currency");
        map.put(IndexedDocumentModel.ISSUE_DATE, "issueDate");
        map.put(IndexedDocumentModel.AMOUNT, "amount");
        map.put(IndexedDocumentModel.SUPPLIER_NAME, "supplierName");
        map.put(IndexedDocumentModel.SUPPLIER_ASSIGNED_ID, "supplierAssignedId");
        map.put(IndexedDocumentModel.CUSTOMER_NAME, "customerName");
        map.put(IndexedDocumentModel.CUSTOMER_ASSIGNED_ID, "customerAssignedId");

        // Document User
        map.put(DocumentUserModel.CHECKED, "starred");
        map.put(DocumentUserModel.VIEWED, "viewed");
        map.put(DocumentUserModel.CHECKED, "checked");
        map.put(DocumentUserModel.TAGS, "tags");

        return map;
    }

    @Override
    public String apply(String fieldName) {
        if (prefix == null) {
            return mapper.getOrDefault(fieldName, fieldName);
        } else {
            return prefix + "." + mapper.getOrDefault(fieldName, fieldName);
        }
    }

}
