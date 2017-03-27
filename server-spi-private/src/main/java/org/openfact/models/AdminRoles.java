package org.openfact.models;

public class AdminRoles {

    public static final String ADMIN = "admin";

    public static final String CREATE_ACCOUNTING_CUSTOMER_PARTY = "create-accounting-customer-party";

    public static final String VIEW_ACCOUNTING_CUSTOMER_PARTY = "view-accounting-customer-party";
    public static final String VIEW_DOCUMENTS = "view-documents";

    public static final String MANAGE_ACCOUNTING_CUSTOMER_PARTY = "manage-accounting-customer-party";
    public static final String MANAGE_DOCUMENTS = "manage-documents";

    public static final String[] ALL_ORGANIZATION_ROLES = {
            /*
             * */
            VIEW_ACCOUNTING_CUSTOMER_PARTY, VIEW_DOCUMENTS,

            /*
             * */
            MANAGE_ACCOUNTING_CUSTOMER_PARTY, MANAGE_DOCUMENTS,
    };

}
