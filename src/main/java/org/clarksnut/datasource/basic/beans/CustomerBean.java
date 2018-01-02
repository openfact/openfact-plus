package org.clarksnut.datasource.basic.beans;

public class CustomerBean {

    private String name;
    private String assignedId;
    private PostalAddressBean postalAddress;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

    public PostalAddressBean getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(PostalAddressBean postalAddress) {
        this.postalAddress = postalAddress;
    }
}
