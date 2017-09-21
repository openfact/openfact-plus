package org.openfact.services.resources;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.openfact.models.*;
import org.openfact.models.utils.ModelToRepresentation;
import org.openfact.representation.idm.SpaceRepresentation;
import org.openfact.services.ErrorResponseException;
import org.openfact.services.managers.DocumentManager;

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
    public Response importDocument(final MultipartFormDataInput multipartFormDataInput) throws ErrorResponseException {
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
                documentModel = documentManager.importDocument(inputStream);
            } catch (ParseExceptionModel e) {
                throw new ErrorResponseException("Could not process file", Response.Status.BAD_REQUEST);
            } catch (StorageException e) {
                throw new ErrorResponseException("Could not write files to storage", Response.Status.INTERNAL_SERVER_ERROR);
            }

            // Return result
            return Response.ok(documentModel).build();
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
    public SpaceRepresentation getSpace(@PathParam("documentId") String documentId) {
        DocumentModel ublDocument = getDocumentById(documentId);
        return modelToRepresentation.toRepresentation(ublDocument, uriInfo).toSpaceRepresentation();
    }

    @DELETE
    @Path("{documentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteSpace(@PathParam("documentId") String documentId) {
        DocumentModel ublDocument = getDocumentById(documentId);
        documentManager.removeDocument(ublDocument);
    }

}
