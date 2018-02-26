package org.clarksnut.mapper.document.pe;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueTimeType;

import java.util.*;
import java.util.stream.Collectors;

public class PEUtils {

    public static String getSupplierName(SupplierPartyType accountingSupplierParty) {
        if (accountingSupplierParty != null) {
            PartyType party = accountingSupplierParty.getParty();
            if (party != null) {
                if (!party.getPartyLegalEntity().isEmpty()) {
                    return party.getPartyLegalEntity().stream()
                            .map(f -> f.getRegistrationName().getValue())
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(","));
                } else if (!party.getPartyName().isEmpty()) {
                    return party.getPartyName().stream()
                            .map(f -> f.getName().getValue())
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
                            .map(f -> f.getRegistrationName().getValue())
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(","));
                } else if (!party.getPartyName().isEmpty()) {
                    return party.getPartyName().stream()
                            .map(f -> f.getName().getValue())
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

    public static Date toDate(oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueDateType issueDate, Optional<IssueTimeType> issueTimeType) {
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
        if (addressType.getDistrict() != null) {
            city.add(addressType.getDistrict().getValue());
        } else if (addressType.getCitySubdivisionName() != null) {
            city.add(addressType.getCitySubdivisionName().getValue());
        }
        if (addressType.getCityName() != null) {
            city.add(addressType.getCityName().getValue());
        }
        if (addressType.getCountrySubentity() != null) {
            city.add(addressType.getCountrySubentity().getValue());
        }
        return String.join(", ", city);
    }

    public static String toCityString(oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType addressType) {
        List<String> city = new ArrayList<>();
        if (addressType.getDistrict() != null) {
            city.add(addressType.getDistrict().getValue());
        } else if (addressType.getCitySubdivisionName() != null) {
            city.add(addressType.getCitySubdivisionName().getValue());
        }
        if (addressType.getCityName() != null) {
            city.add(addressType.getCityName().getValue());
        }
        if (addressType.getCountrySubentity() != null) {
            city.add(addressType.getCountrySubentity().getValue());
        }
        return String.join(", ", city);
    }
}
