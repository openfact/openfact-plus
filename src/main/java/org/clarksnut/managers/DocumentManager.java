package org.clarksnut.managers;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentProvider;
import org.clarksnut.documents.ImportedDocumentModel;
import org.clarksnut.documents.exceptions.AlreadyImportedDocumentException;
import org.clarksnut.documents.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.documents.exceptions.IsNotUBLDocumentException;
import org.clarksnut.documents.exceptions.UnsupportedDocumentTypeException;
import org.clarksnut.files.*;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapped.DocumentBean;
import org.clarksnut.mapper.document.DocumentMapperProvider;
import org.clarksnut.mapper.document.DocumentMapperProviderFactory;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.SpaceProvider;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.exceptions.ForbiddenExceptionModel;
import org.jboss.logging.Logger;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

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

    public DocumentModel getDocumentById(UserModel user, String documentId) throws ForbiddenExceptionModel {
        DocumentModel document = documentProvider.getDocument(documentId);
        if (document == null) return null;

        Set<SpaceModel> permittedSpaces = user.getAllPermitedSpaces();
        if (permittedSpaces.contains(document.getSupplier()) || permittedSpaces.contains(document.getCustomer())) {
            return document;
        } else {
            throw new ForbiddenExceptionModel();
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

        // Supplier
        String supplierAssignedId = bean.getSupplierAssignedId();
        SpaceModel supplier = spaceProvider.getByAssignedId(supplierAssignedId);
        if (supplier == null) {
            supplier = spaceProvider.addSpace(supplierAssignedId, supplierAssignedId);
        }

        // Customer
        String customerAssignedId = bean.getCustomerAssignedId();
        SpaceModel customer = null;
        if (customerAssignedId != null) {
            customer = spaceProvider.getByAssignedId(customerAssignedId);
            if (customer == null) {
                customer = spaceProvider.addSpace(customerAssignedId, customerAssignedId);
            }
        }

        // Add document
        return documentProvider.addDocument(documentType, importedDocument, bean, supplier, customer);
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
