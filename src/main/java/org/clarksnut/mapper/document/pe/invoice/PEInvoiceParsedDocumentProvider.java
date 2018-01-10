package org.clarksnut.mapper.document.pe.invoice;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapperProvider;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;

import javax.xml.bind.JAXBException;

public class PEInvoiceParsedDocumentProvider implements DocumentMapperProvider {

    private static final Logger logger = Logger.getLogger(PEInvoiceParsedDocumentProvider.class);

    @Override
    public String getGroup() {
        return "peru";
    }

    @Override
    public String getSupportedDocumentType() {
        return "Invoice";
    }

    @Override
    public DocumentMapped map(XmlUBLFileModel file) {
        InvoiceType invoiceType;
        try {
            invoiceType = ClarksnutModelUtils.unmarshall(file.getDocument(), InvoiceType.class);
        } catch (JAXBException e) {
            return null;
        }

        return new DocumentMapped() {
            @Override
            public DocumentBean getBean() {
                return new PEInvoiceBeanAdapter(invoiceType);
            }

            @Override
            public Object getType() {
                return invoiceType;
            }
        };
    }

}
