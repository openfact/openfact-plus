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

    public static ProveedorBean toSupplier(SupplierPartyType supplierPartyType) {
        ProveedorBean bean = new ProveedorBean();

        bean.setIdAssignado(supplierPartyType.getCustomerAssignedAccountID().getValue());
        bean.setNombre(supplierPartyType.getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        TipoDocumentoEntidad.getByCode(supplierPartyType.getAdditionalAccountID().get(0).getValue()).ifPresent(c -> {
            bean.setTipoDocumento(c.getDenominacion());
        });
        bean.setDireccion(toAddress(supplierPartyType.getParty().getPostalAddress()));

        return bean;
    }

    public static ClienteBean toCustomer(CustomerPartyType customerPartyType) {
        ClienteBean bean = new ClienteBean();

        // Assigned ID is not always present
        CustomerAssignedAccountIDType customerAssignedAccountID = customerPartyType.getCustomerAssignedAccountID();
        if (customerAssignedAccountID != null) {
            bean.setIdAssignado(customerAssignedAccountID.getValue());
        }

        bean.setNombre(customerPartyType.getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());

        // Document Type
        List<AdditionalAccountIDType> additionalAccountIDTypes = customerPartyType.getAdditionalAccountID();
        if (additionalAccountIDTypes != null && !additionalAccountIDTypes.isEmpty()) {
            TipoDocumentoEntidad.getByCode(additionalAccountIDTypes.get(0).getValue()).ifPresent(c -> {
                bean.setTipoDocumento(c.getDenominacion());
            });
        }

        // Address is optional for customers
        AddressType addressType = customerPartyType.getParty().getPostalAddress();
        if (addressType != null) {
            bean.setDireccion(toAddress(addressType));
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
