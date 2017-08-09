package org.openfact.models.db.es.mapper;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.openfact.models.FileModel;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.openfact.models.db.es.DocumentMapper;
import org.openfact.models.db.es.entity.IndexedDocumentEntity;
import org.w3c.dom.Document;

import javax.ejb.Stateless;

@Stateless
@MapperType(value = "Invoice")
public class InvoiceReader implements DocumentMapper {

    @Override
    public DocumentEntity buildEntity(Document document, FileModel fileModel) {
        InvoiceType invoiceType = UBL21Reader.invoice().read(document);
        IndexedDocumentEntity entity = new IndexedDocumentEntity();
        entity.setFileId(fileModel.getId());
        return entity;
    }

}
