package org.clarksnut.mapper.document.basic.invoice;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapperProvider;
import org.jboss.logging.Logger;

public class BasicInvoiceParsedDocumentProvider implements DocumentMapperProvider {

    private static final Logger logger = Logger.getLogger(BasicInvoiceParsedDocumentProvider.class);

    @Override
    public String getGroup() {
        return "basic";
    }

    @Override
    public String getSupportedDocumentType() {
        return "Invoice";
    }

    @Override
    public DocumentMapped map(XmlUBLFileModel file) {
        InvoiceType invoiceType = UBL21Reader.invoice().read(file.getDocument());
        if (invoiceType == null) {
            return null;
        }

        return new DocumentMapped() {
            @Override
            public DocumentBean getBean() {
                return new BasicInvoiceBeanAdapter(invoiceType);
            }

            @Override
            public Object getType() {
                return invoiceType;
            }
        };
    }

}
