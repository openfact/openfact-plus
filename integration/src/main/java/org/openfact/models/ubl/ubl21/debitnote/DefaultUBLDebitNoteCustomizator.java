package org.openfact.models.ubl.ubl21.debitnote;

import org.openfact.models.ubl.ubl21.qualifiers.UBLDocumentType;
import org.openfact.provider.ProviderType;

import javax.ejb.Stateless;

@Stateless
@ProviderType("default")
@UBLDocumentType("DEBIT_NOTE")
public class DefaultUBLDebitNoteCustomizator implements UBLDebitNoteCustomizator {

    @Override
    public Customizator customizator() {
        return null;
    }

}
