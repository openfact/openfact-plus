package org.openfact.repositories.user;

public interface MailUBLMessage {

    byte[] getXml() throws MailReadException;

    byte[] getInvoice() throws MailReadException;

}
