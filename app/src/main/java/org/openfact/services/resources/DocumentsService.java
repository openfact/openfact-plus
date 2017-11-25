package org.openfact.services.resources;

import jodd.io.ZipBuilder;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.keycloak.representations.AccessToken;
import org.openfact.documents.DocumentModel;
import org.openfact.documents.DocumentProvider;
import org.openfact.documents.DocumentProviderType;
import org.openfact.documents.exceptions.PreexistedDocumentException;
import org.openfact.documents.exceptions.UnreadableDocumentException;
import org.openfact.documents.exceptions.UnsupportedDocumentTypeException;
import org.openfact.files.FileModel;
import org.openfact.files.FileProvider;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.files.exceptions.FileStorageException;
import org.openfact.managers.DocumentManager;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.report.ExportFormat;
import org.openfact.report.ReportTemplateConfiguration;
import org.openfact.report.ReportTemplateProvider;
import org.openfact.report.exceptions.ReportException;
import org.openfact.representations.idm.DocumentRepresentation;
import org.openfact.representations.idm.GenericDataRepresentation;
import org.openfact.services.ErrorResponse;
import org.openfact.services.ErrorResponseException;
import org.openfact.services.resources.utils.PATCH;
import org.openfact.services.util.SSOContext;
import org.openfact.utils.ModelToRepresentation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Path("/documents")
public class DocumentsService {

    private static final Logger logger = Logger.getLogger(DocumentsService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private UserProvider userProvider;

    @Inject
    private FileProvider fileProvider;

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

    private UserModel getUserByIdentityID(String identityID) {
        UserModel user = userProvider.getUserByIdentityID(identityID);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
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
                return tmp[1].trim().replaceAll("\"", "");
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

    @PATCH
    @Path("massive")
    @Produces(MediaType.APPLICATION_JSON)
    public void updateDocuments(GenericDataRepresentation<List<DocumentRepresentation.Data>> representation) {
        representation.getData().forEach(documentRepresentation -> {
            DocumentModel document = getDocumentById(documentRepresentation.getId());

            DocumentRepresentation.Attributes attributes = documentRepresentation.getAttributes();
            if (attributes.getStarred() != null) {
                document.setStarred(attributes.getStarred());
            }
            if (attributes.getTags() != null && !attributes.getTags().isEmpty()) {
                document.setTags(attributes.getTags());
            }
        });
    }

    @GET
    @Path("/massive/download")
    @Produces("application/zip")
    public Response downloadDocuments(@QueryParam("documents") List<String> documentsId) {
        Set<FileModel> files = documentsId.stream()
                .map(this::getDocumentById)
                .map(document -> fileProvider.getFile(document.getFileId()))
                .collect(Collectors.toSet());

        ZipBuilder zipInMemory = ZipBuilder.createZipInMemory();
        for (FileModel file : files) {
            try {
                byte[] bytes = file.getFile();
                zipInMemory.add(bytes).path(file.getFilename()).save();
            } catch (FileFetchException e) {
                logger.error("could not fetch file for zip", e);
            } catch (IOException e) {
                logger.error("Could not add file to zip", e);
            }
        }
        byte[] zip = zipInMemory.toBytes();

        Response.ResponseBuilder response = Response.ok(zip);
        response.header("Content-Disposition", "attachment; filename=\"" + UUID.randomUUID().toString() + ".xml\"");
        return response.build();
    }

    @GET
    @Path("/massive/print")
    @Produces("application/zip")
    public Response printDocuments(
            @Context final HttpServletRequest httpServletRequest,
            @QueryParam("documents") List<String> documentsId,
            @QueryParam("theme") String theme,
            @QueryParam("format") @DefaultValue("PDF") String format
    ) {
        SSOContext ssoContext = new SSOContext(httpServletRequest);
        AccessToken accessToken = ssoContext.getParsedAccessToken();

        String kcUserID = (String) accessToken.getOtherClaims().get("userID");
        UserModel user = getUserByIdentityID(kcUserID);

        //
        ExportFormat exportFormat = ExportFormat.valueOf(format.toUpperCase());

        Set<DocumentModel> documents = documentsId.stream()
                .map(this::getDocumentById)
                .collect(Collectors.toSet());

        ReportTemplateConfiguration reportConfig = ReportTemplateConfiguration.builder()
                .themeName(theme)
                .locale(user.getLanguage() != null ? new Locale(user.getLanguage()) : Locale.ENGLISH)
                .build();

        ZipBuilder zipInMemory = ZipBuilder.createZipInMemory();
        for (DocumentModel document : documents) {
            try {
                byte[] bytes = reportTemplateProvider.getReport(reportConfig, document, exportFormat);
                zipInMemory.add(bytes).path(document.getAssignedId() + "." + exportFormat.getExtension()).save();
            } catch (FileFetchException | ReportException e) {
                logger.error("Could not generate a report", e);
            } catch (IOException e) {
                logger.error("Could not add file to zip", e);
            }
        }
        byte[] zip = zipInMemory.toBytes();

        Response.ResponseBuilder response = Response.ok(zip);
        response.header("Content-Disposition", "attachment; filename=\"" + UUID.randomUUID().toString() + ".xml\"");
        return response.build();
    }

    @GET
    @Path("/{documentId}/download")
    @Produces("application/xml")
    public Response getXml(@PathParam("documentId") String documentId) {
        DocumentModel document = getDocumentById(documentId);

        FileModel file = fileProvider.getFile(document.getFileId());

        byte[] reportBytes;
        try {
            reportBytes = file.getFile();
        } catch (FileFetchException e) {
            return ErrorResponse.error("Could not fetch file, please try again", Response.Status.INTERNAL_SERVER_ERROR);
        }

        Response.ResponseBuilder response = Response.ok(reportBytes);
        response.header("Content-Disposition", "attachment; filename=\"" + document.getAssignedId() + ".xml\"");
        return response.build();
    }

    @GET
    @Path("/{documentId}/print")
    public Response printDocument(
            @Context final HttpServletRequest httpServletRequest,
            @PathParam("documentId") String documentId,
            @QueryParam("theme") String theme,
            @QueryParam("format") @DefaultValue("PDF") String format) {
        SSOContext ssoContext = new SSOContext(httpServletRequest);
        AccessToken accessToken = ssoContext.getParsedAccessToken();

        String kcUserID = (String) accessToken.getOtherClaims().get("userID");
        UserModel user = getUserByIdentityID(kcUserID);

        //
        ExportFormat exportFormat = ExportFormat.valueOf(format.toUpperCase());

        DocumentModel document = getDocumentById(documentId);
        ReportTemplateConfiguration reportConfig = ReportTemplateConfiguration.builder()
                .themeName(theme)
                .locale(user.getLanguage() != null ? new Locale(user.getLanguage()) : Locale.ENGLISH)
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
