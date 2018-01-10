package org.clarksnut.datasource.basic;

import com.helger.xsds.ccts.cct.schemamodule.TextType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueTimeType;
import org.clarksnut.datasource.basic.beans.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

public class BasicDatasourceUtils {

    public static SupplierBean toSupplier(SupplierPartyType supplierPartyType) {
        SupplierBean bean = new SupplierBean();

        bean.setAssignedId(supplierPartyType.getCustomerAssignedAccountIDValue());

        PartyType partyType = supplierPartyType.getParty();
        if (partyType != null) {
            // Name
            String registrationName = partyType.getPartyLegalEntity()
                    .stream()
                    .map(PartyLegalEntityType::getRegistrationNameValue)
                    .collect(Collectors.joining(","));
            bean.setName(registrationName);

            // Address
            AddressType addressType = partyType.getPostalAddress();
            if (addressType != null) {
                bean.setPostalAddress(toAddress(addressType));
            }
        }

        return bean;
    }

    public static CustomerBean toCustomer(CustomerPartyType customerPartyType) {
        CustomerBean bean = new CustomerBean();

        bean.setAssignedId(customerPartyType.getCustomerAssignedAccountIDValue());

        PartyType partyType = customerPartyType.getParty();
        if (partyType != null) {
            // Name
            String registrationName = partyType.getPartyLegalEntity()
                    .stream()
                    .map(PartyLegalEntityType::getRegistrationNameValue)
                    .collect(Collectors.joining(","));
            bean.setName(registrationName);

            // Address
            AddressType addressType = partyType.getPostalAddress();
            if (addressType != null) {
                bean.setPostalAddress(toAddress(addressType));
            }
        }

        return bean;
    }

    public static PostalAddressBean toAddress(AddressType addressType) {
        PostalAddressBean bean = new PostalAddressBean();

        bean.setId(addressType.getIDValue());
        bean.setStreetName(addressType.getStreetNameValue());
        bean.setCitySubdivisionName(addressType.getCitySubdivisionNameValue());
        bean.setCityName(addressType.getCityNameValue());
        bean.setCountrySubentity(addressType.getCountrySubentityValue());

        // Country might not being present
        CountryType countryType = addressType.getCountry();
        if (countryType != null) {
            bean.setCountryIdentificationCode(countryType.getIdentificationCodeValue());
        }

        return bean;
    }

    public static MonetaryTotalBean toMonetaryTotal(MonetaryTotalType monetaryTotalType) {
        MonetaryTotalBean bean = new MonetaryTotalBean();

        if (monetaryTotalType.getPayableAmountValue() != null) {
            bean.setPayableAmount(monetaryTotalType.getPayableAmountValue().floatValue());
        }
        if (monetaryTotalType.getAllowanceTotalAmountValue() != null) {
            bean.setAllowanceTotal(monetaryTotalType.getAllowanceTotalAmountValue().floatValue());
        }
        if (monetaryTotalType.getChargeTotalAmountValue() != null) {
            bean.setChargeTotal(monetaryTotalType.getChargeTotalAmountValue().floatValue());
        }

        return bean;
    }

    public static Date toDate(IssueDateType issueDate, Optional<IssueTimeType> issueTimeType) {
        XMLGregorianCalendar issueDateXmlGregorianCalendar = issueDate.getValue();
        if (issueDateXmlGregorianCalendar != null) {
            Date date = issueDateXmlGregorianCalendar.toGregorianCalendar().getTime();
            if (issueTimeType.isPresent()) {
                XMLGregorianCalendar issueTimeXmlGregorianCalendar = issueTimeType.get().getValue();
                if (issueTimeXmlGregorianCalendar != null) {
                    Date time = issueTimeXmlGregorianCalendar.toGregorianCalendar().getTime();

                    Calendar issueDateCalendar = Calendar.getInstance();
                    issueDateCalendar.setTime(date);

                    Calendar issueTimeCalendar = Calendar.getInstance();
                    issueTimeCalendar.setTime(time);

                    issueDateCalendar.set(Calendar.HOUR_OF_DAY, issueTimeCalendar.get(Calendar.HOUR_OF_DAY));
                    issueDateCalendar.set(Calendar.MINUTE, issueTimeCalendar.get(Calendar.MINUTE));
                    issueDateCalendar.set(Calendar.SECOND, issueTimeCalendar.get(Calendar.SECOND));

                    date = issueDateCalendar.getTime();
                }
            }
            return date;
        }
        return null;
    }

    public static void addDescriptionAndProductCode(LineBean lineBean, Optional<ItemType> itemType) {
        if (itemType.isPresent()) {
            String description = itemType.get().getDescription().stream()
                    .map(TextType::getValue)
                    .collect(Collectors.joining(", "));
            lineBean.setDescription(description);

            ItemIdentificationType itemIdentificationType = itemType.get().getSellersItemIdentification();
            if (itemIdentificationType != null) {
                lineBean.setProductCode(itemIdentificationType.getIDValue());
            }
        }
    }

}
