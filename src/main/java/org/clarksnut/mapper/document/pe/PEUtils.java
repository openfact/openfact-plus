package org.clarksnut.mapper.document.pe;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueTimeType;

import java.util.*;

public class PEUtils {

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
