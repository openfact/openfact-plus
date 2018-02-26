package org.clarksnut.mapper.document.basic;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueTimeType;

import java.util.*;
import java.util.stream.Collectors;

public class BasicUtils {

    public static String getSupplierName(SupplierPartyType accountingSupplierParty) {
        if (accountingSupplierParty != null) {
            PartyType party = accountingSupplierParty.getParty();
            if (party != null) {
                if (!party.getPartyLegalEntity().isEmpty()) {
                    return party.getPartyLegalEntity().stream()
                            .map(PartyLegalEntityType::getRegistrationNameValue)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(","));
                } else if (!party.getPartyName().isEmpty()) {
                    return party.getPartyName().stream()
                            .map(PartyNameType::getNameValue)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(","));
                }
            }
        }
        return null;
    }

    public static String getCustomerName(CustomerPartyType accountingCustomerParty) {
        if (accountingCustomerParty != null) {
            PartyType party = accountingCustomerParty.getParty();
            if (party != null) {
                if (!party.getPartyLegalEntity().isEmpty()) {
                    return party.getPartyLegalEntity().stream()
                            .map(PartyLegalEntityType::getRegistrationNameValue)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(", "));
                } else if (!party.getPartyName().isEmpty()) {
                    return party.getPartyName().stream()
                            .map(PartyNameType::getNameValue)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(","));
                }
            }
        }
        return null;
    }

    public static Date toDate(IssueDateType issueDate, Optional<IssueTimeType> issueTimeType) {
        Date date = issueDate.getValue().toGregorianCalendar().getTime();
        if (issueTimeType.isPresent()) {
            Date time = issueTimeType.get().getValue().toGregorianCalendar().getTime();

            Calendar issueDateCalendar = Calendar.getInstance();
            issueDateCalendar.setTime(date);

            Calendar issueTimeCalendar = Calendar.getInstance();
            issueTimeCalendar.setTime(time);

            issueDateCalendar.set(Calendar.HOUR_OF_DAY, issueTimeCalendar.get(Calendar.HOUR_OF_DAY));
            issueDateCalendar.set(Calendar.MINUTE, issueTimeCalendar.get(Calendar.MINUTE));
            issueDateCalendar.set(Calendar.SECOND, issueTimeCalendar.get(Calendar.SECOND));

            date = issueDateCalendar.getTime();
        }
        return date;
    }

    public static String toCityString(AddressType addressType) {
        List<String> city = new ArrayList<>();
        if (addressType.getDistrictValue() != null) {
            city.add(addressType.getDistrictValue());
        } else if (addressType.getCitySubdivisionNameValue() != null) {
            city.add(addressType.getCitySubdivisionNameValue());
        }
        if (addressType.getCityNameValue() != null) {
            city.add(addressType.getCityNameValue());
        }
        if (addressType.getCountrySubentityValue() != null) {
            city.add(addressType.getCountrySubentityValue());
        }
        return String.join(", ", city);
    }

}
