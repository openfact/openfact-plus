package org.openfact.services.resources;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.openfact.documents.DocumentModel;
import org.openfact.documents.DocumentProvider;
import org.openfact.documents.DocumentProviderType;
import org.openfact.documents.exceptions.PreexistedDocumentException;
import org.openfact.documents.exceptions.UnreadableDocumentException;
import org.openfact.documents.exceptions.UnsupportedDocumentTypeException;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.files.exceptions.FileStorageException;
import org.openfact.representations.idm.DocumentRepresentation;
import org.openfact.services.ErrorResponseException;
import org.openfact.services.managers.DocumentManager;
import org.openfact.utils.ModelToRepresentation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Stateless
@Path("/documents")
public class DocumentsService {

    private static final Logger logger = Logger.getLogger(DocumentsService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private DocumentProvider documentProvider;

    @Inject
    private DocumentManager documentManager;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    private DocumentModel getDocumentById(String documentId) {
        DocumentModel ublDocument = documentProvider.getDocument(documentId);
        if (ublDocument == null) {
            throw new NotFoundException();
        }
        return ublDocument;
    }

    /**
     * @return javax.ws.rs.core.Response including document persisted information
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public DocumentRepresentation importDocument(final MultipartFormDataInput multipartFormDataInput) throws ErrorResponseException {
        Map<String, List<InputPart>> formParts = multipartFormDataInput.getFormDataMap();
        List<InputPart> inputParts = formParts.get("file");

        for (InputPart inputPart : inputParts) {
            // Extract file
            MultivaluedMap<String, String> headers = inputPart.getHeaders();
            String fileName = parseFileName(headers);
            InputStream inputStream;
            try {
                inputStream = inputPart.getBody(InputStream.class, null);
            } catch (IOException e) {
                throw new ErrorResponseException("Could not read file " + fileName, Response.Status.BAD_REQUEST);
            }

            // Save document
            DocumentModel documentModel = null;
            try {
                documentModel = documentManager.importDocument(inputStream, DocumentProviderType.USER);
            } catch (UnsupportedDocumentTypeException | UnreadableDocumentException e) {
                throw new ErrorResponseException("Unsupported type", Response.Status.BAD_REQUEST);
            } catch (FileStorageException e) {
                throw new ErrorResponseException("Could not save file", Response.Status.INTERNAL_SERVER_ERROR);
            } catch (FileFetchException e) {
                throw new ErrorResponseException("Could not fetch file", Response.Status.INTERNAL_SERVER_ERROR);
            } catch (IOException e) {
                throw new ErrorResponseException("Could not read file", Response.Status.INTERNAL_SERVER_ERROR);
            } catch (PreexistedDocumentException e) {
                throw new ErrorResponseException("There is a preexisted document, you cannot override it", Response.Status.CONFLICT);
            }

            // Return result
            return modelToRepresentation.toRepresentation(documentModel, uriInfo).toSpaceRepresentation();
        }

        throw new ErrorResponseException("Could not find any file to process");
    }

    private String parseFileName(MultivaluedMap<String, String> headers) {
        String[] contentDispositionHeader = headers.getFirst("Content-Disposition").split(";");
        for (String name : contentDispositionHeader) {
            if ((name.trim().startsWith("filename"))) {
                String[] tmp = name.split("=");
                String fileName = tmp[1].trim().replaceAll("\"", "");
                return fileName;
            }
        }
        return null;
    }

    @GET
    @Path("{documentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public DocumentRepresentation getDocument(@PathParam("documentId") String documentId) {
        DocumentModel document = getDocumentById(documentId);
        return modelToRepresentation.toRepresentation(document, uriInfo).toSpaceRepresentation();
    }

    @DELETE
    @Path("{documentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteDocument(@PathParam("documentId") String documentId) {
        DocumentModel ublDocument = getDocumentById(documentId);
        documentManager.removeDocument(ublDocument);
    }

}
