package org.openfact.datasource;

import org.openfact.documents.DocumentModel;
import org.openfact.files.XmlFileModel;
import org.openfact.files.exceptions.FileFetchException;

import javax.ejb.Stateless;

@Stateless
@DatasourceType(datasource = "BaseDS")
public class BaseDatasourceProvider implements DatasourceProvider {

    @Override
    public boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException {
        return true;
    }

    @Override
    public Object getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        BaseBean bean = new BaseBean();
        bean.setId(document.getId());
        bean.setType(document.getType());
        bean.setAssignedId(document.getAssignedId());
        bean.setAmount(document.getAmount());
        bean.setCurrency(document.getCurrency());
        bean.setIssueDate(document.getIssueDate());
        bean.setSupplierName(document.getSupplierName());
        bean.setSupplierAssignedId(document.getSupplierAssignedId());
        bean.setCustomerName(document.getCustomerName());
        bean.setCustomerAssignedId(document.getCustomerAssignedId());

        return bean;
    }

}
