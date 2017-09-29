package org.openfact.models.db.es.reader.pe.retention;

import org.jboss.logging.Logger;
import org.openfact.models.InteractType;
import org.openfact.models.XmlUBLFileModel;
import org.openfact.models.db.es.DocumentReader;
import org.openfact.models.db.es.GenericDocument;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.openfact.models.db.es.entity.DocumentSpaceEntity;
import org.openfact.models.db.es.reader.MapperType;
import org.openfact.models.db.es.reader.pe.common.PEUtils;
import org.openfact.models.db.es.reader.pe.common.jaxb.retention.RetentionType;
import org.openfact.models.db.jpa.entity.SpaceEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.util.Arrays;
import java.util.HashSet;

@Stateless
@MapperType(value = "Retention")
public class PERetentionReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(PERetentionReader.class);

    @Inject
    private PEUtils peUtils;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        RetentionType retentionType;
        try {
            retentionType = OpenfactModelUtils.unmarshall(file.getDocument(), RetentionType.class);
        } catch (JAXBException e) {
            return null;
        }

        SpaceEntity senderSpaceEntity = peUtils.getSpace(retentionType.getAgentParty());
        SpaceEntity receiverSpaceEntity = peUtils.getSpace(retentionType.getReceiverParty());


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
        documentEntity.setAssignedId(retentionType.getId().getValue());
        documentEntity.setSpaces(new HashSet<>(Arrays.asList(documentSpaceSenderEntity, documentSpaceReceiverEntity)));

        return new GenericDocument() {
            @Override
            public DocumentEntity getEntity() {
                return documentEntity;
            }

            @Override
            public Object getJaxb() {
                return retentionType;
            }
        };
    }

}
