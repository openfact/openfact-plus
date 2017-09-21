package org.openfact.models.db.es.reader.invoice;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.openfact.models.FileModel;
import org.openfact.models.ModelException;
import org.openfact.models.DocumentModel;
import org.openfact.models.db.es.DocumentReader;
import org.openfact.models.db.es.GenericDocument;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.openfact.models.db.es.reader.MapperType;
import org.openfact.models.utils.OpenfactModelUtils;
import org.w3c.dom.Document;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Stateless
@MapperType(value = "Invoice")
public class DefaultInvoiceReader implements DocumentReader {

    @Inject
    private EntityManager em;

    @Override
    public GenericDocument read(FileModel file) {
        byte[] bytes;
        Document document;
        try {
            bytes = file.getFile();
            document = OpenfactModelUtils.toDocument(bytes);
        } catch (Exception e) {
            throw new ModelException("Could not read bytes from file");
        }

        InvoiceType invoiceType = UBL21Reader.invoice().read(document);

        DocumentEntity entity = new DocumentEntity();
        entity.setFileId(file.getId());
        entity.setAssignedId(invoiceType != null ? invoiceType.getIDValue() : null);

        return new GenericDocument() {
            @Override
            public DocumentEntity getEntity() {
                return entity;
            }

            @Override
            public Object getType() {
                return invoiceType;
            }
        };
    }

}
