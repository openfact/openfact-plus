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
import org.openfact.managers.DocumentManager;
import org.openfact.reports.ExportFormat;
import org.openfact.reports.ReportTemplateConfiguration;
import org.openfact.reports.ReportTemplateProvider;
import org.openfact.reports.exceptions.ReportException;
import org.openfact.representations.idm.DocumentReportQueryRepresentation;
import org.openfact.representations.idm.DocumentRepresentation;
import org.openfact.services.ErrorResponse;
import org.openfact.services.ErrorResponseException;
import org.openfact.services.resources.utils.PATCH;
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

    @Inject
    private ReportTemplateProvider reportTemplateProvider;

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

    @PATCH
    @Path("{documentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public DocumentRepresentation updateDocument(@PathParam("documentId") String documentId, DocumentRepresentation documentRepresentation) {
        DocumentModel document = getDocumentById(documentId);

        DocumentRepresentation.Data data = documentRepresentation.getData();
        DocumentRepresentation.Attributes attributes = data.getAttributes();

        if (attributes.getStarred() != null) {
            document.setStarred(attributes.getStarred());
        }
        if (attributes.getTags() != null && !attributes.getTags().isEmpty()) {
            document.setTags(attributes.getTags());
        }
        return modelToRepresentation.toRepresentation(document, uriInfo).toSpaceRepresentation();
    }

    @DELETE
    @Path("{documentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteDocument(@PathParam("documentId") String documentId) {
        DocumentModel ublDocument = getDocumentById(documentId);
        documentManager.removeDocument(ublDocument);
    }

    @POST
    @Path("/{documentId}/report")
    public Response getPdf(@PathParam("documentId") String documentId, DocumentReportQueryRepresentation query) {
        if (query.getFormat() == null) {
            query.setFormat("PDF");
        }
        ExportFormat exportFormat = ExportFormat.valueOf(query.getFormat().toUpperCase());

        DocumentModel document = getDocumentById(documentId);
        ReportTemplateConfiguration reportConfig = ReportTemplateConfiguration.builder()
                .themeName(query.getFormat())
                .addAllAttributes(query.getAttributes())
                .build();

        byte[] reportBytes;
        try {
            reportBytes = reportTemplateProvider.getReport(reportConfig, document, exportFormat);
        } catch (ReportException | FileFetchException e) {
            return ErrorResponse.error("Could not generate report, please try again", Response.Status.INTERNAL_SERVER_ERROR);
        }

        Response.ResponseBuilder response = Response.ok(reportBytes);
        switch (exportFormat) {
            case PDF:
                response.type("application/pdf");
                response.header("Content-Disposition", "attachment; filename=\"" + document.getAssignedId() + ".pdf\"");
                break;
            case HTML:
                response.type("application/html");
                break;
        }

        return response.build();
    }

}
