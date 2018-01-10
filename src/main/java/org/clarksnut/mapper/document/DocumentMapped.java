package org.clarksnut.mapper.document;

import java.util.Date;

public interface DocumentMapped {

    Object getType();

    DocumentBean getBean();

    interface DocumentBean {

        String getAssignedId();

        Float getAmount();

        Float getTax();

        String getCurrency();

        Date getIssueDate();

        String getSupplierName();

        String getSupplierAssignedId();

        String getSupplierStreetAddress();

        String getSupplierCity();

        String getSupplierCountry();

        String getCustomerName();

        String getCustomerAssignedId();

        String getCustomerStreetAddress();

        String getCustomerCity();

        String getCustomerCountry();
    }
}
