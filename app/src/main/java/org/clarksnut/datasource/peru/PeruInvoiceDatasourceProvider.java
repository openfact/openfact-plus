package org.clarksnut.datasource.peru;


import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.datasource.peru.beans.BeanUtils;
import org.clarksnut.datasource.peru.beans.InvoiceBean;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.utils.ClarksnutModelUtils;

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
            return ClarksnutModelUtils.unmarshall(file.getDocument(), InvoiceType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
