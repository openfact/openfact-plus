package org.clarksnut.datasource.peru.beans;

public class SupplierBean {

    private String supplierName;
    private String supplierAssignedId;
    private String supplierDocumentType;
    private PostalAddressBean postalAddress;

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierAssignedId() {
        return supplierAssignedId;
    }

    public void setSupplierAssignedId(String supplierAssignedId) {
        this.supplierAssignedId = supplierAssignedId;
    }

    public PostalAddressBean getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(PostalAddressBean postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getSupplierDocumentType() {
        return supplierDocumentType;
    }

    public void setSupplierDocumentType(String supplierDocumentType) {
        this.supplierDocumentType = supplierDocumentType;
    }
}
