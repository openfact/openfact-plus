package org.openfact.models.db.es.reader.pe.invoice;

import com.helger.xml.namespace.MapBasedNamespaceContext;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.jboss.logging.Logger;
import org.openfact.models.*;
import org.openfact.models.db.es.DocumentReader;
import org.openfact.models.db.es.ESDocumentProvider;
import org.openfact.models.db.es.GenericDocument;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.openfact.models.db.es.entity.DocumentSpaceEntity;
import org.openfact.models.db.es.reader.LocationType;
import org.openfact.models.db.es.reader.MapperType;
import org.openfact.models.db.es.reader.pe.common.PEUtils;
import org.openfact.models.db.jpa.entity.SpaceEntity;
import org.openfact.models.utils.OpenfactModelUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Stateless
@MapperType(value = "Invoice")
@LocationType(value = "peru")
public class PEInvoiceReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(PEInvoiceReader.class);

    @Inject
    private PEUtils peUtils;

    @Override
    public GenericDocument read(XmlUblFileModel file) throws ModelFetchException, ModelParseException {
        byte[] bytes = file.getFile();

        Document document;
        try {
            document = OpenfactModelUtils.toDocument(bytes);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            logger.error("Could not parse document event when is " + XmlUblFileModel.class.getName());
            throw new ModelException("Could not read document");
        }

        InvoiceType invoiceType;
        try {
            invoiceType = OpenfactModelUtils.unmarshall(document, InvoiceType.class);
        } catch (JAXBException e) {
            throw new ModelParseException("Could not parse document, it could be caused by invalid xml content");
        }

        SpaceEntity senderSpaceEntity = peUtils.getSpace(invoiceType.getAccountingSupplierParty());
        SpaceEntity receiverSpaceEntity = peUtils.getSpace(invoiceType.getAccountingCustomerParty());

        DocumentEntity documentEntity = new DocumentEntity();

        DocumentSpaceEntity documentSpaceSenderEntity = new DocumentSpaceEntity();
        documentSpaceSenderEntity.setId(OpenfactModelUtils.generateId());
        documentSpaceSenderEntity.setType(InteractType.SENDER);
        documentSpaceSenderEntity.setSpace(senderSpaceEntity);
        documentSpaceSenderEntity.setDocument(documentEntity);

        DocumentSpaceEntity documentSpaceReceiverEntity = new DocumentSpaceEntity();
        documentSpaceReceiverEntity.setId(OpenfactModelUtils.generateId());
        documentSpaceReceiverEntity.setType(InteractType.RECEIVER);
        documentSpaceReceiverEntity.setSpace(receiverSpaceEntity);
        documentSpaceReceiverEntity.setDocument(documentEntity);

        documentEntity.setFileId(file.getId());
        documentEntity.setAssignedId(invoiceType.getID().getValue());
        documentEntity.setSpaces(new HashSet<>(Arrays.asList(documentSpaceSenderEntity, documentSpaceReceiverEntity)));

        return new GenericDocument() {
            @Override
            public DocumentEntity getEntity() {
                return documentEntity;
            }

            @Override
            public Object getType() {
                return invoiceType;
            }
        };
    }

}
