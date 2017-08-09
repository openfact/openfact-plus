package org.openfact.repositories.user;

public interface UserRepositoryElementModel {
    byte[] getXml() throws UserRepositoryReadException;
    byte[] getInvoice() throws UserRepositoryReadException;
}
