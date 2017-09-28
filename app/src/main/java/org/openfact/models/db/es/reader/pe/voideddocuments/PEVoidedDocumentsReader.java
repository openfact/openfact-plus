package org.openfact.models.db.es.reader.pe.voideddocuments;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.jboss.logging.Logger;
import org.openfact.models.ModelException;
import org.openfact.models.ModelFetchException;
import org.openfact.models.ModelParseException;
import org.openfact.models.XmlUblFileModel;
import org.openfact.models.db.es.DocumentReader;
import org.openfact.models.db.es.GenericDocument;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.openfact.models.db.es.reader.LocationType;
import org.openfact.models.db.es.reader.MapperType;
import org.openfact.models.db.jpa.entity.SpaceEntity;
import org.openfact.models.utils.OpenfactModelUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

@Stateless
@MapperType(value = "VoidedDocuments")
@LocationType(value = "peru")
public class PEVoidedDocumentsReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(PEVoidedDocumentsReader.class);

    @Inject
    private EntityManager em;

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

        VoidedDocumentsType voidedDocumentsType;
        try {
            voidedDocumentsType = OpenfactModelUtils.unmarshall(document, VoidedDocumentsType.class);
        } catch (JAXBException e) {
            throw new ModelParseException("Could not parse document, it could be caused by invalid xml content");
        }

        SpaceEntity spaceEntity = getSpace(voidedDocumentsType);
        if (spaceEntity == null) {
            spaceEntity = new SpaceEntity();
            spaceEntity.setId(OpenfactModelUtils.generateId());
            spaceEntity.setAssignedId(voidedDocumentsType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
            spaceEntity.setName(voidedDocumentsType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
            em.persist(spaceEntity);
        }

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setFileId(file.getId());
        documentEntity.setAssignedId(voidedDocumentsType.getID().getValue());
        documentEntity.setSpace(spaceEntity);

        return new GenericDocument() {
            @Override
            public DocumentEntity getEntity() {
                return documentEntity;
            }

            @Override
            public Object getType() {
                return voidedDocumentsType;
            }
        };
    }

    private SpaceEntity getSpace(VoidedDocumentsType voidedDocumentsType) {
        SupplierPartyType accountingSupplierParty = voidedDocumentsType.getAccountingSupplierParty();
        if (accountingSupplierParty == null) return null;

        String assignedAccountIDValue = accountingSupplierParty.getCustomerAssignedAccountID().getValue();
        TypedQuery<SpaceEntity> typedQuery = em.createNamedQuery("getSpaceByAssignedId", SpaceEntity.class);
        typedQuery.setParameter("assignedId", assignedAccountIDValue);
        List<SpaceEntity> resultList = typedQuery.getResultList();
        if (resultList.isEmpty()) return null;
        return resultList.get(0);
    }

}
