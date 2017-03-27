package org.openfact.models.ubl.ubl21.invoice;

import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.openfact.models.ubl.UBLCustomizator;

public interface UBLInvoiceCustomizatorFactory<T extends InvoiceType> extends UBLCustomizator<T> {
}
