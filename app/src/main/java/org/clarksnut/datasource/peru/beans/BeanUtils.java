package org.clarksnut.datasource.peru.beans;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CountryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AdditionalAccountIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CustomerAssignedAccountIDType;
import org.clarksnut.datasource.peru.types.TipoDocumentoEntidad;

import java.util.List;

public class BeanUtils {

    public static SupplierBean toSupplier(SupplierPartyType supplierPartyType) {
        SupplierBean bean = new SupplierBean();

        bean.setSupplierAssignedId(supplierPartyType.getCustomerAssignedAccountID().getValue());
        bean.setSupplierName(supplierPartyType.getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        TipoDocumentoEntidad.getByCode(supplierPartyType.getAdditionalAccountID().get(0).getValue()).ifPresent(c -> {
            bean.setSupplierDocumentType(c.getDenominacion());
        });
        bean.setPostalAddress(toAddress(supplierPartyType.getParty().getPostalAddress()));

        return bean;
    }

    public static CustomerBean toCustomer(CustomerPartyType customerPartyType) {
        CustomerBean bean = new CustomerBean();

        // Assigned ID is not always present
        CustomerAssignedAccountIDType customerAssignedAccountID = customerPartyType.getCustomerAssignedAccountID();
        if (customerAssignedAccountID != null) {
            bean.setCustomerAssignedId(customerAssignedAccountID.getValue());
        }

        bean.setCustomerName(customerPartyType.getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());

        // Document Type
        List<AdditionalAccountIDType> additionalAccountIDTypes = customerPartyType.getAdditionalAccountID();
        if (additionalAccountIDTypes != null && !additionalAccountIDTypes.isEmpty()) {
            TipoDocumentoEntidad.getByCode(additionalAccountIDTypes.get(0).getValue()).ifPresent(c -> {
                bean.setCustomerDocumentType(c.getDenominacion());
            });
        }

        // Address is optional for customers
        AddressType addressType = customerPartyType.getParty().getPostalAddress();
        if (addressType != null) {
            bean.setPostalAddress(toAddress(addressType));
        }

        return bean;
    }

    public static PostalAddressBean toAddress(AddressType addressType) {
        PostalAddressBean bean = new PostalAddressBean();

        bean.setId(addressType.getID().getValue());
        bean.setStreetName(addressType.getStreetName().getValue());
        bean.setCitySubdivisionName(addressType.getCitySubdivisionName().getValue());
        bean.setCityName(addressType.getCityName().getValue());
        bean.setCountrySubentity(addressType.getCountrySubentity().getValue());

        // Country might not being present
        CountryType countryType = addressType.getCountry();
        if (countryType != null) {
            bean.setCountryIdentificationCode(countryType.getIdentificationCode().getValue());
        }

        return bean;
    }

}
