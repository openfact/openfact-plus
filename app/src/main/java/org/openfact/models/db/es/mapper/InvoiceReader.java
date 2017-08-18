package org.openfact.models.db.es.mapper;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.openfact.models.FileModel;
import org.openfact.models.ModelException;
import org.openfact.models.db.es.DocumentMapper;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.openfact.models.db.es.entity.IndexedDocumentEntity;
import org.openfact.models.utils.OpenfactModelUtils;
import org.w3c.dom.Document;

import javax.ejb.Stateless;

@Stateless
@MapperType(value = "Invoice")
public class InvoiceReader implements DocumentMapper {

    @Override
    public DocumentEntity buildEntity(FileModel file) {
        byte[] bytes;
        Document document;
        try {
            bytes = file.getFile();
            document = OpenfactModelUtils.toDocument(bytes);
        } catch (Exception e) {
            throw new ModelException("Could not read bytes from file");
        }

        InvoiceType invoiceType = UBL21Reader.invoice().read(document);

        IndexedDocumentEntity entity = new IndexedDocumentEntity();
        entity.setType("Invoice");
        entity.setFileId(file.getId());
        entity.setAssignedId(invoiceType != null ? invoiceType.getIDValue() : null);
        return entity;
    }

}
