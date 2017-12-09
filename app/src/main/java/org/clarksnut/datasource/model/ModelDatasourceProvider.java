package org.clarksnut.datasource.model;

import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;

import javax.ejb.Stateless;

@Stateless
@DatasourceType(datasource = "ModelDS")
public class ModelDatasourceProvider implements DatasourceProvider {

    @Override
    public boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException {
        return true;
    }

    @Override
    public Datasource getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        ModelDatasource bean = new ModelDatasource();
        bean.setId(document.getId());
        bean.setType(document.getType());
        bean.setAssignedId(document.getAssignedId());
        bean.setAmount(document.getAmount());
        bean.setTax(document.getTax());
        bean.setCurrency(document.getCurrency());
        bean.setIssueDate(document.getIssueDate());
        bean.setSupplierName(document.getSupplierName());
        bean.setSupplierAssignedId(document.getSupplierAssignedId());
        bean.setCustomerName(document.getCustomerName());
        bean.setCustomerAssignedId(document.getCustomerAssignedId());

        bean.setSupplierStreetAddress(document.getSupplierStreetAddress());
        bean.setSupplierCity(document.getSupplierCity());
        bean.setSupplierCountry(document.getSupplierCountry());

        bean.setCustomerStreetAddress(document.getCustomerStreetAddress());
        bean.setCustomerCity(document.getCustomerCity());
        bean.setCustomerCountry(document.getCustomerCountry());

        bean.setLocation("www.clarksnut.com");

        return bean;
    }

}
