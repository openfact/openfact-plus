package org.openfact.models.db.es.reader.pe.summarydocuments;

import org.jboss.logging.Logger;
import org.openfact.models.InteractType;
import org.openfact.models.XmlUBLFileModel;
import org.openfact.models.db.es.DocumentReader;
import org.openfact.models.db.es.GenericDocument;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.openfact.models.db.es.entity.DocumentSpaceEntity;
import org.openfact.models.db.es.reader.MapperType;
import org.openfact.models.db.es.reader.pe.common.PEUtils;
import org.openfact.models.db.jpa.entity.SpaceEntity;
import org.openfact.models.utils.OpenfactModelUtils;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.util.Arrays;
import java.util.HashSet;

@Stateless
@MapperType(value = "SummaryDocuments")
public class PESummaryDocumentsReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(PESummaryDocumentsReader.class);

    @Inject
    private PEUtils peUtils;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        SummaryDocumentsType summaryDocumentsType;
        try {
            summaryDocumentsType = OpenfactModelUtils.unmarshall(file.getDocument(), SummaryDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }

        SpaceEntity senderSpaceEntity = peUtils.getSpace(summaryDocumentsType.getAccountingSupplierParty());


        DocumentEntity documentEntity = new DocumentEntity();

        DocumentSpaceEntity documentSpaceSenderEntity = new DocumentSpaceEntity();
        documentSpaceSenderEntity.setId(OpenfactModelUtils.generateId());
        documentSpaceSenderEntity.setType(InteractType.SENDER);
        documentSpaceSenderEntity.setSpace(senderSpaceEntity);
        documentSpaceSenderEntity.setDocument(documentEntity);

        documentEntity.setFileId(file.getId());
        documentEntity.setAssignedId(summaryDocumentsType.getID().getValue());
        documentEntity.setSpaces(new HashSet<>(Arrays.asList(documentSpaceSenderEntity)));

        return new GenericDocument() {
            @Override
            public DocumentEntity getEntity() {
                return documentEntity;
            }

            @Override
            public Object getJaxb() {
                return summaryDocumentsType;
            }
        };
    }

}
