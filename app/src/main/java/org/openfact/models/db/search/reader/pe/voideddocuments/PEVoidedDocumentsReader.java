package org.openfact.models.db.search.reader.pe.voideddocuments;

import org.jboss.logging.Logger;
import org.openfact.models.InteractType;
import org.openfact.models.XmlUBLFileModel;
import org.openfact.models.db.search.DocumentReader;
import org.openfact.models.db.search.GenericDocument;
import org.openfact.models.db.search.entity.DocumentEntity;
import org.openfact.models.db.search.entity.DocumentSpaceEntity;
import org.openfact.models.db.search.reader.SupportedType;
import org.openfact.models.db.search.reader.pe.common.PEUtils;
import org.openfact.models.db.jpa.entity.SpaceEntity;
import org.openfact.models.utils.OpenfactModelUtils;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.util.Arrays;
import java.util.HashSet;

@Stateless
@SupportedType(value = "VoidedDocuments")
public class PEVoidedDocumentsReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(PEVoidedDocumentsReader.class);

    @Inject
    private PEUtils peUtils;

    @Override
    public String getSupportedDocumentType() {
        return "VoidedDocuments";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        VoidedDocumentsType voidedDocumentsType;
        try {
            voidedDocumentsType = OpenfactModelUtils.unmarshall(file.getDocument(), VoidedDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }

        SpaceEntity senderSpaceEntity = peUtils.getSpace(voidedDocumentsType.getAccountingSupplierParty());


        DocumentEntity documentEntity = new DocumentEntity();

        DocumentSpaceEntity documentSpaceSenderEntity = new DocumentSpaceEntity();
        documentSpaceSenderEntity.setId(OpenfactModelUtils.generateId());
        documentSpaceSenderEntity.setType(InteractType.SENDER);
        documentSpaceSenderEntity.setSpace(senderSpaceEntity);
        documentSpaceSenderEntity.setDocument(documentEntity);

        documentEntity.setFileId(file.getId());
        documentEntity.setAssignedId(voidedDocumentsType.getID().getValue());
        documentEntity.setSpaces(new HashSet<>(Arrays.asList(documentSpaceSenderEntity)));

        return new GenericDocument() {
            @Override
            public DocumentEntity getEntity() {
                return documentEntity;
            }

            @Override
            public Object getJaxb() {
                return voidedDocumentsType;
            }
        };
    }

}
