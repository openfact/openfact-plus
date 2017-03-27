package org.openfact.models.ubl.ubl21.creditnote;

import org.openfact.models.ubl.ubl21.qualifiers.UBLDocumentType;
import org.openfact.provider.ProviderType;

import javax.ejb.Stateless;

@Stateless
@ProviderType("default")
@UBLDocumentType("CREDIT_NOTE")
public class DefaultUBLCreditNoteCustomizator implements UBLCreditNoteCustomizator {

    @Override
    public Customizator customizator() {
        return null;
    }

}
