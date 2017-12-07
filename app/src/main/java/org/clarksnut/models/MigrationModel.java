package org.clarksnut.models;

public interface MigrationModel {

    String getStoredVersion();

    void setStoredVersion(String version);

}
