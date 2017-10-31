package org.openfact.documents.reader.basic.invoice;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.jboss.logging.Logger;
import org.openfact.documents.DocumentReader;
import org.openfact.documents.GenericDocument;
import org.openfact.documents.jpa.entity.DocumentEntity;
import org.openfact.documents.reader.SupportedType;
import org.openfact.files.XmlUBLFileModel;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.Map;

@Stateless
@SupportedType(value = "Invoice")
public class BasicInvoiceReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(BasicInvoiceReader.class);

    @Override
    public String getSupportedDocumentType() {
        return "Invoice";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        InvoiceType invoiceType = UBL21Reader.invoice().read(file.getDocument());
        if (invoiceType == null) {
            return null;
        }

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setAssignedId(invoiceType.getIDValue());
        documentEntity.setSupplierAssignedId(invoiceType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setSupplierName(invoiceType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCustomerAssignedId(invoiceType.getAccountingCustomerParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setCustomerName(invoiceType.getAccountingCustomerParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setCurrency(invoiceType.getLegalMonetaryTotal().getPayableAmount().getCurrencyID());
        documentEntity.setAmount(invoiceType.getLegalMonetaryTotal().getPayableAmount().getValue().floatValue());
        documentEntity.setIssueDate(invoiceType.getIssueDate().getValue().toGregorianCalendar().getTime());

        return new GenericDocument() {
            @Override
            public DocumentEntity getEntity() {
                return documentEntity;
            }

            @Override
            public Object getJaxb() {
                return invoiceType;
            }
        };
    }

}
