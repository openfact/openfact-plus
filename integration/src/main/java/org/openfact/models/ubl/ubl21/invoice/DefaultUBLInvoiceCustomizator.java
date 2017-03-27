package org.openfact.models.ubl.ubl21.invoice;

import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.openfact.models.ubl.ubl21.qualifiers.UBLDocumentType;
import org.openfact.provider.ProviderType;

import javax.ejb.Stateless;

@Stateless
@ProviderType("default")
@UBLDocumentType("INVOICE")
public class DefaultUBLInvoiceCustomizator implements UBLInvoiceCustomizator {

    @Override
    public Customizator customizator() {
        return (Customizator<InvoiceType>) (customerParty, document, invoiceType) -> {

        };
    }

}
