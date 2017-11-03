package org.openfact.datasource.peru;


import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.openfact.files.*;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.utils.OpenfactModelUtils;
import org.openfact.datasource.DatasourceProvider;
import org.openfact.datasource.DatasourceType;
import org.openfact.datasource.peru.beans.*;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@DatasourceType(datasource = "PeruInvoiceDS")
public class PeruInvoiceDatasourceProvider implements DatasourceProvider {

    @Override
    public Object getDatasource(XmlFileModel file) throws FileFetchException {
        InvoiceType invoiceType;
        try {
            invoiceType = OpenfactModelUtils.unmarshall(file.getDocument(), InvoiceType.class);
        } catch (JAXBException e) {
            return null;
        }

        InvoiceBean bean = new InvoiceBean();
        bean.setAssignedId(invoiceType.getID().getValue());
        bean.setIssueDate(invoiceType.getIssueDate().getValue().toGregorianCalendar().getTime());
        bean.setCurrency(invoiceType.getDocumentCurrencyCode().getValue());
        bean.setSupplier(BeanUtils.toSupplier(invoiceType.getAccountingSupplierParty()));
        bean.setCustomer(BeanUtils.toSupplier(invoiceType.getAccountingCustomerParty()));
        return bean;
    }

}
