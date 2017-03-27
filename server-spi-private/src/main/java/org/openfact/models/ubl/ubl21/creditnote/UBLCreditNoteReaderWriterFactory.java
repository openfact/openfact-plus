package org.openfact.models.ubl.ubl21.creditnote;

import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import org.openfact.models.ubl.UBLReaderWriter;

public interface UBLCreditNoteReaderWriterFactory<T extends CreditNoteType> extends UBLReaderWriter<T> {
}
