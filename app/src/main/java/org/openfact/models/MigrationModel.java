package org.openfact.models;

public interface MigrationModel {

    String getStoredVersion();
    void setStoredVersion(String version);

}
