package org.clarksnut.mapper.document.basic.invoice;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapperProvider;
import org.clarksnut.models.exceptions.ImpossibleToUnmarshallException;
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
    public DocumentMapped map(XmlUBLFileModel file) throws ImpossibleToUnmarshallException {
        InvoiceType invoiceType = null;
        try {
            invoiceType = UBL21Reader.invoice().read(file.getDocument());
        } catch (Exception e) {
            // Nothing to do
        }
        if (invoiceType == null) {
            throw new ImpossibleToUnmarshallException("Could not marshall to:" + InvoiceType.class.getName());
        }

        final InvoiceType type = invoiceType;
        return new DocumentMapped() {
            @Override
            public DocumentBean getBean() {
                return new BasicInvoiceBeanAdapter(type);
            }

            @Override
            public Object getType() {
                return type;
            }
        };
    }

}
