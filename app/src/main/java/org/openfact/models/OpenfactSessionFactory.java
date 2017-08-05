package org.openfact.models;

public interface OpenfactSessionFactory {

    OpenfactSession create();

    void close();

}
