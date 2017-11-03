package org.openfact.datasource.peru;


import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.openfact.datasource.DatasourceProvider;
import org.openfact.datasource.DatasourceType;
import org.openfact.datasource.peru.beans.BeanUtils;
import org.openfact.datasource.peru.beans.InvoiceBean;
import org.openfact.documents.DocumentModel;
import org.openfact.files.XmlFileModel;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@DatasourceType(datasource = "PeruInvoiceDS")
public class PeruInvoiceDatasourceProvider implements DatasourceProvider {

    @Override
    public boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException {
        return read(file) != null;
    }

    @Override
    public Object getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        InvoiceType invoiceType = read(file);
        if (invoiceType == null) {
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

    private InvoiceType read(XmlFileModel file) throws FileFetchException {
        try {
            return OpenfactModelUtils.unmarshall(file.getDocument(), InvoiceType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
