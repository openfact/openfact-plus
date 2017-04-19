package org.openfact.models.ubl;

import org.openfact.models.DocumentModel;

public interface UBLCustomizator<T> {

    Customizator<T> customizator();

    interface Customizator<T> {
        void customize(DocumentModel document, T t);
    }

}
