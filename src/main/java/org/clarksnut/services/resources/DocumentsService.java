package org.clarksnut.services.resources;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentProvider;
import org.clarksnut.files.FileProvider;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.report.ReportTemplateProvider;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

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
//    @POST
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.APPLICATION_JSON)
//    public DocumentRepresentation importDocument(final MultipartFormDataInput multipartFormDataInput) throws ErrorResponseException {
//        Map<String, List<InputPart>> formParts = multipartFormDataInput.getFormDataMap();
//        List<InputPart> inputParts = formParts.get("file");
//
//        for (InputPart inputPart : inputParts) {
//            // Extract file
//            MultivaluedMap<String, String> headers = inputPart.getHeaders();
//            String fileName = parseFileName(headers);
//            InputStream inputStream;
//            try {
//                inputStream = inputPart.getBody(InputStream.class, null);
//            } catch (IOException e) {
//                throw new ErrorResponseException("Could not read file " + fileName, Response.Status.BAD_REQUEST);
//            }
//
//            // Save document
//            DocumentModel documentModel = null;
//            try {
//                documentModel = documentManager.importDocument(inputStream, DocumentProviderType.USER_COLLECTOR);
//            } catch (DocumentNotImportedException | DocumentNotImportedButSavedForFutureException e) {
//                throw new ErrorResponseException("Could not import the file", Response.Status.BAD_REQUEST);
//            } catch (FileStorageException e) {
//                throw new ErrorResponseException("Could not save file", Response.Status.INTERNAL_SERVER_ERROR);
//            } catch (FileFetchException e) {
//                throw new ErrorResponseException("Could not fetch file", Response.Status.INTERNAL_SERVER_ERROR);
//            } catch (IOException e) {
//                throw new ErrorResponseException("Could not read file", Response.Status.INTERNAL_SERVER_ERROR);
//            }
//
//            // Return result
//            return modelToRepresentation.toRepresentation(documentModel, uriInfo).toSpaceRepresentation();
//        }
//
//        throw new ErrorResponseException("Could not find any file to process");
//    }

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

//    @GET
//    @Path("{documentId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public DocumentRepresentation getDocument(@PathParam("documentId") String documentId) {
//        DocumentModel document = getDocumentById(documentId);
//        return modelToRepresentation.toRepresentation(document, uriInfo).toSpaceRepresentation();
//    }

//    @PATCH
//    @Path("{documentId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public DocumentRepresentation updateDocument(@PathParam("documentId") String documentId, DocumentRepresentation documentRepresentation) {
//        DocumentModel document = getDocumentById(documentId);
//
//        DocumentRepresentation.Data data = documentRepresentation.getData();
//        DocumentRepresentation.Attributes attributes = data.getAttributes();
//
//        if (attributes.getStarred() != null) {
//            document.setStarred(attributes.getStarred());
//        }
//        if (attributes.getTags() != null && !attributes.getTags().isEmpty()) {
//            document.setTags(attributes.getTags());
//        }
//        return modelToRepresentation.toRepresentation(document, uriInfo).toSpaceRepresentation();
//    }
//
//    @DELETE
//    @Path("{documentId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public void deleteDocument(@PathParam("documentId") String documentId) {
//        DocumentModel ublDocument = getDocumentById(documentId);
//        documentManager.removeDocument(ublDocument);
//    }

//    @PATCH
//    @Path("massive")
//    @Produces(MediaType.APPLICATION_JSON)
//    public void updateDocuments(GenericDataRepresentation<List<DocumentRepresentation.Data>> representation) {
//        representation.getData().forEach(documentRepresentation -> {
//            DocumentModel document = getDocumentById(documentRepresentation.getId());
//
//            DocumentRepresentation.Attributes attributes = documentRepresentation.getAttributes();
//            if (attributes.getStarred() != null) {
//                document.setStarred(attributes.getStarred());
//            }
//            if (attributes.getTags() != null && !attributes.getTags().isEmpty()) {
//                document.setTags(attributes.getTags());
//            }
//        });
//    }

//    @GET
//    @Path("/massive/download")
//    @Produces("application/zip")
//    public Response downloadDocuments(@QueryParam("documents") List<String> documentsId) {
//        ZipBuilder zipInMemory = ZipBuilder.createZipInMemory();
//
//        documentsId.stream()
//                .map(this::getDocumentById)
//                .forEach(document -> {
//                    FileModel file = fileProvider.getFileAsBytes(document.getFileId());
//                    try {
//                        zipInMemory.add(file.getFileAsBytes()).path(document.getAssignedId()).save();
//                    } catch (IOException e) {
//                        logger.error("Could not add file to zip", e);
//                    } catch (FileFetchException e) {
//                        logger.error("could not fetch file for zip", e);
//                    }
//                });
//
//        byte[] zip = zipInMemory.toBytes();
//
//        Response.ResponseBuilder response = Response.ok(zip);
//        response.header("Content-Disposition", "attachment; filename=\"" + UUID.randomUUID().toString() + ".xml\"");
//        return response.build();
//    }

//    @GET
//    @Path("/massive/print")
//    @Produces("application/zip")
//    public Response printDocuments(
//            @Context final HttpServletRequest httpServletRequest,
//            @QueryParam("documents") List<String> documentsId,
//            @QueryParam("theme") String theme,
//            @QueryParam("format") @DefaultValue("PDF") String format
//    ) {
//        SSOContext ssoContext = new SSOContext(httpServletRequest);
//        AccessToken accessToken = ssoContext.getParsedAccessToken();
//
//        String kcUserID = (String) accessToken.getOtherClaims().get("userID");
//        UserModel user = getUserByIdentityID(kcUserID);
//
//        //
//        ExportFormat exportFormat = ExportFormat.valueOf(format.toUpperCase());
//
//        Set<DocumentModel> documents = documentsId.stream()
//                .map(this::getDocumentById)
//                .collect(Collectors.toSet());
//
//        ReportTemplateConfiguration reportConfig = ReportTemplateConfiguration.builder()
//                .themeName(theme)
//                .locale(user.getLanguage() != null ? new Locale(user.getLanguage()) : Locale.ENGLISH)
//                .build();
//
//        ZipBuilder zipInMemory = ZipBuilder.createZipInMemory();
//        for (DocumentModel document : documents) {
//            try {
//                byte[] bytes = reportTemplateProvider.getReport(reportConfig, document, exportFormat);
//                zipInMemory.add(bytes).path(document.getAssignedId() + "." + exportFormat.getExtension()).save();
//            } catch (FileFetchException | ReportException e) {
//                logger.error("Could not generate a report", e);
//            } catch (IOException e) {
//                logger.error("Could not add file to zip", e);
//            }
//        }
//        byte[] zip = zipInMemory.toBytes();
//
//        Response.ResponseBuilder response = Response.ok(zip);
//        response.header("Content-Disposition", "attachment; filename=\"" + UUID.randomUUID().toString() + ".xml\"");
//        return response.build();
//    }

//    @GET
//    @Path("/{documentId}/download")
//    @Produces("application/xml")
//    public Response getXml(@PathParam("documentId") String documentId) {
//        DocumentModel document = getDocumentById(documentId);
//
//        FileModel file = fileProvider.getFileAsBytes(document.getFileId());
//
//        byte[] reportBytes;
//        try {
//            reportBytes = file.getFileAsBytes();
//        } catch (FileFetchException e) {
//            return ErrorResponse.error("Could not fetch file, please try again", Response.Status.INTERNAL_SERVER_ERROR);
//        }
//
//        Response.ResponseBuilder response = Response.ok(reportBytes);
//        response.header("Content-Disposition", "attachment; filename=\"" + document.getAssignedId() + ".xml\"");
//        return response.build();
//    }

//    @GET
//    @Path("/{documentId}/print")
//    public Response printDocument(
//            @Context final HttpServletRequest httpServletRequest,
//            @PathParam("documentId") String documentId,
//            @QueryParam("theme") String theme,
//            @QueryParam("format") @DefaultValue("PDF") String format) {
//        SSOContext ssoContext = new SSOContext(httpServletRequest);
//        AccessToken accessToken = ssoContext.getParsedAccessToken();
//
//        String kcUserID = (String) accessToken.getOtherClaims().get("userID");
//        UserModel user = getUserByIdentityID(kcUserID);
//
//        //
//        ExportFormat exportFormat = ExportFormat.valueOf(format.toUpperCase());
//
//        DocumentModel document = getDocumentById(documentId);
//        ReportTemplateConfiguration reportConfig = ReportTemplateConfiguration.builder()
//                .themeName(theme)
//                .locale(user.getLanguage() != null ? new Locale(user.getLanguage()) : Locale.ENGLISH)
//                .build();
//
//        byte[] reportBytes;
//        try {
//            reportBytes = reportTemplateProvider.getReport(reportConfig, document, exportFormat);
//        } catch (ReportException | FileFetchException e) {
//            return ErrorResponse.error("Could not generate report, please try again", Response.Status.INTERNAL_SERVER_ERROR);
//        }
//
//        Response.ResponseBuilder response = Response.ok(reportBytes);
//        switch (exportFormat) {
//            case PDF:
//                response.type("application/pdf");
//                response.header("Content-Disposition", "attachment; filename=\"" + document.getAssignedId() + ".pdf\"");
//                break;
//            case HTML:
//                response.type("application/html");
//                break;
//        }
//
//        return response.build();
//    }

}
