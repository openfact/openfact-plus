package org.clarksnut.managers;

import org.clarksnut.files.*;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapped.DocumentBean;
import org.clarksnut.mapper.document.DocumentMapperProvider;
import org.clarksnut.mapper.document.DocumentMapperProviderFactory;
import org.clarksnut.models.*;
import org.clarksnut.models.exceptions.*;
import org.jboss.logging.Logger;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class DocumentManager {

    private static final Logger logger = Logger.getLogger(DocumentManager.class);

    @Inject
    private DocumentProvider documentProvider;

    @Inject
    private SpaceProvider spaceProvider;

    @Inject
    @ConfigurationValue("clarksnut.document.mapper.default")
    private Optional<String> defaultDocumentMapper;

    public DocumentModel getDocumentById(UserModel user, String documentId) throws ModelForbiddenException {
        DocumentModel document = documentProvider.getDocument(documentId);
        if (document == null) return null;

        Set<String> permittedSpaceAssignedIds = user.getAllPermitedSpaces().stream().map(SpaceModel::getAssignedId).collect(Collectors.toSet());
        if (permittedSpaceAssignedIds.contains(document.getSupplierAssignedId()) || permittedSpaceAssignedIds.contains(document.getCustomerAssignedId())) {
            return document;
        } else {
            throw new ModelForbiddenException();
        }
    }

    public DocumentModel addDocument(ImportedDocumentModel importedDocument) throws IsNotUBLDocumentException, UnsupportedDocumentTypeException, AlreadyImportedDocumentException, ImpossibleToUnmarshallException {
        XmlUBLFileModel ublFile = new FlyWeightXmlUBLFileModel(
                new BasicXmlUBLFileModel(
                        new FlyWeightXmlFileModel(
                                new BasicXmlFileModel(
                                        new FlyWeightFileModel(importedDocument.getFile())
                                )
                        )
                )
        );

        String documentType = null;
        try {
            documentType = ublFile.getDocumentType();
        } catch (Exception e) {
            // Nothing to do
        }
        if (documentType == null || documentType.trim().isEmpty()) {
            throw new IsNotUBLDocumentException("Could not get valid DocumentType");
        }

        DocumentMapped map = mapDocument(documentType, ublFile);
        DocumentBean bean = map.getBean();

        // Add document
        return documentProvider.addDocument(documentType, importedDocument, bean);
    }

    private DocumentMapped mapDocument(String documentType, XmlUBLFileModel ublFile) throws UnsupportedDocumentTypeException, ImpossibleToUnmarshallException {
        String mapper = defaultDocumentMapper.orElse("basic");

        DocumentMapperProvider provider = DocumentMapperProviderFactory.getInstance().getParsedDocumentProvider(mapper, documentType);
        if (provider == null && !mapper.equals("basic")) {
            provider = DocumentMapperProviderFactory.getInstance().getParsedDocumentProvider("basic", documentType);
        }
        if (provider == null) {
            throw new UnsupportedDocumentTypeException("Could not find a DocumentMapperProvider for group[" + mapper + "/basic] " + "documentType[" + documentType + "]");
        }

        return provider.map(ublFile);
    }
}
