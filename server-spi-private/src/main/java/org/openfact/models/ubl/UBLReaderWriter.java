package org.openfact.models.ubl;

import org.openfact.models.AccountingCustomerPartyModel;
import org.w3c.dom.Document;

public interface UBLReaderWriter<T> {

    UBLReader<T> reader();

    UBLWriter<T> writer();

    interface UBLReader<T> {
        T read(byte[] bytes);

        T read(Document document);
    }

    interface UBLWriter<T> {
        Document write(AccountingCustomerPartyModel customerParty, T t);
    }

}