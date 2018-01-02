package org.clarksnut.datasource.basic.beans;

public class PostalAddressBean {

    private String id;
    private String streetName;
    private String citySubdivisionName;
    private String cityName;
    private String countrySubentity;
    private String countryIdentificationCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCitySubdivisionName() {
        return citySubdivisionName;
    }

    public void setCitySubdivisionName(String citySubdivisionName) {
        this.citySubdivisionName = citySubdivisionName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountrySubentity() {
        return countrySubentity;
    }

    public void setCountrySubentity(String countrySubentity) {
        this.countrySubentity = countrySubentity;
    }

    public String getCountryIdentificationCode() {
        return countryIdentificationCode;
    }

    public void setCountryIdentificationCode(String countryIdentificationCode) {
        this.countryIdentificationCode = countryIdentificationCode;
    }

}
