package org.clarksnut.documents.jpa;

import org.clarksnut.documents.DocumentModel;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchDocumentUserFields {

    public static String toDocumentSearchField(String name, String... prefix) {
        String prefixes = Stream.of(prefix).collect(Collectors.joining("."));
        String fieldName = name;

        if (DocumentModel.TYPE.equals(name)) {
            fieldName = "type";
        }
        if (DocumentModel.ASSIGNED_ID.equals(name)) {
            fieldName = "assignedId";
        }
        if (DocumentModel.CURRENCY.equals(name)) {
            fieldName = "currency";
        }
        if (DocumentModel.ISSUE_DATE.equals(name)) {
            fieldName = "issueDate";
        }
        if (DocumentModel.AMOUNT.equals(name)) {
            fieldName = "amount";
        }

        if (DocumentModel.SUPPLIER_NAME.equals(name)) {
            fieldName = "supplierName";
        }
        if (DocumentModel.SUPPLIER_ASSIGNED_ID.equals(name)) {
            fieldName = "supplierAssignedId";
        }

        if (DocumentModel.CUSTOMER_ASSIGNED_ID.equals(name)) {
            fieldName = "customerAssignedId";
        }
        if (DocumentModel.CUSTOMER_NAME.equals(name)) {
            fieldName = "customerName";
        }

        return prefixes != null && !prefixes.isEmpty() ? prefixes + "." + fieldName : fieldName;
    }

}
