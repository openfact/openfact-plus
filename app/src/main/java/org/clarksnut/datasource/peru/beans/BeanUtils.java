package org.clarksnut.datasource.peru.beans;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.SupplierPartyType;

public class BeanUtils {

    public static SupplierBean toSupplier(SupplierPartyType supplierPartyType) {
        SupplierBean bean = new SupplierBean();
        bean.setSupplierName(supplierPartyType.getCustomerAssignedAccountID().getValue());
        bean.setSupplierName(supplierPartyType.getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        bean.setPostalAddress(toAddress(supplierPartyType.getParty().getPostalAddress()));
        return bean;
    }

    public static CustomerBean toSupplier(CustomerPartyType customerPartyType) {
        CustomerBean bean = new CustomerBean();
        bean.setCustomerAssignedId(customerPartyType.getCustomerAssignedAccountID().getValue());
        bean.setCustomerName(customerPartyType.getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        bean.setPostalAddress(toAddress(customerPartyType.getParty().getPostalAddress()));
        return bean;
    }

    public static PostalAddressBean toAddress(AddressType addressType) {
        PostalAddressBean bean = new PostalAddressBean();
        bean.setId(addressType.getID().getValue());
        bean.setStreetName(addressType.getStreetName().getValue());
        bean.setCitySubdivisionName(addressType.getCitySubdivisionName().getValue());
        bean.setCityName(addressType.getCityName().getValue());
        bean.setCountrySubentity(addressType.getCountrySubentity().getValue());
        bean.setCountryIdentificationCode(addressType.getCountry().getIdentificationCode().getValue());
        return bean;
    }

}
