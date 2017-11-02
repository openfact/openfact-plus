package org.openfact.datasource;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.openfact.files.*;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.datasource.beans.InvoiceBean;

import javax.ejb.Stateless;

@Stateless
@DatasourceType(region = "basic", documentType = "Invoice")
public class BasicInvoiceDatasourceProvider implements DatasourceProvider {

    @Override
    public Object getDatasource(XmlFileModel file) throws FileFetchException {
        InvoiceType invoiceType = UBL21Reader.invoice().read(file.getFile());
        if (invoiceType == null) {
            return null;
        }

        InvoiceBean bean = new InvoiceBean();
        return bean;
    }

}
