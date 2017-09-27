package org.openfact.repositories.user;

public interface MailUblMessageModel {

    byte[] getXml() throws MailReadException;

    byte[] getInvoice() throws MailReadException;

}
