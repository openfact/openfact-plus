package org.openfact.models.broker;

public interface BrokerElementModel {

    byte[] getXml() throws ReadBrokerException;

    byte[] getInvoice() throws ReadBrokerException;

}
