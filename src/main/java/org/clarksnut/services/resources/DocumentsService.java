package org.clarksnut.services.resources;

import jodd.io.ZipBuilder;
import org.clarksnut.documents.*;
import org.clarksnut.files.FileModel;
import org.clarksnut.files.uncompress.exceptions.NotReadableCompressFileException;
import org.clarksnut.managers.DocumentManager;
import org.clarksnut.managers.ImportedDocumentManager;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.query.RangeQuery;
import org.clarksnut.query.TermQuery;
import org.clarksnut.query.TermsQuery;
import org.clarksnut.report.ExportFormat;
import org.clarksnut.report.ReportTemplateConfiguration;
import org.clarksnut.report.ReportTemplateProvider;
import org.clarksnut.report.exceptions.ReportException;
import org.clarksnut.representations.idm.DocumentQueryRepresentation;
import org.clarksnut.representations.idm.DocumentRepresentation;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.services.ErrorResponse;
import org.clarksnut.services.ErrorResponseException;
import org.clarksnut.services.resources.utils.PATCH;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
@Path("/documents")
public class DocumentsService {

    private static final Logger logger = Logger.getLogger(DocumentsService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private UserProvider userProvider;

    @Inject
    private ImportedDocumentManager importedDocumentManager;

    @Inject
    private DocumentManager documentManager;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @Inject
    private ReportTemplateProvider reportTemplateProvider;

    @Inject
    private IndexedDocumentProvider indexedDocumentProvider;

    private DocumentModel getDocumentById(UserModel user, String documentId) {
        DocumentModel document = documentManager.getDocumentById(user, documentId);
        if (document == null) {
            throw new NotFoundException();
        }
        return document;
    }

    private UserModel getUser(HttpServletRequest httpServletRequest) {
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();
        String username = accessToken.getPreferredUsername();

        UserModel user = userProvider.getUserByUsername(username);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDocuments(@Context HttpServletRequest httpServletRequest,
                                    @QueryParam("q") String q) throws ErrorResponseException {
        // User context
        UserModel user = getUser(httpServletRequest);

        // Query
        DocumentQueryRepresentation query;
        try {
            query = new DocumentQueryRepresentation(q);
        } catch (ParseException e) {
            return ErrorResponse.error("Bad query request", Response.Status.BAD_REQUEST);
        }

        Map<String, SpaceModel> allPermittedSpaces = new HashMap<>();
        for (SpaceModel space : user.getAllPermitedSpaces()) {
            allPermittedSpaces.put(space.getAssignedId(), space);
        }
        if (query.getSpaces() != null) {
            for (String assignedId : query.getSpaces()) {
                allPermittedSpaces.remove(assignedId);
            }
        }
        if (allPermittedSpaces.isEmpty()) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("totalCount", 0);
            return Response.ok(new GenericDataRepresentation<>(new ArrayList<>(), Collections.emptyMap(), meta)).build();
        }

        // ES
        TermsQuery typeQuery = null;
        if (query.getTypes() != null && !query.getTypes().isEmpty()) {
            typeQuery = new TermsQuery(IndexedDocumentModel.TYPE, query.getTypes());
        }

        TermsQuery currencyQuery = null;
        if (query.getCurrencies() != null && !query.getCurrencies().isEmpty()) {
            currencyQuery = new TermsQuery(IndexedDocumentModel.CURRENCY, query.getCurrencies());
        }

        TermsQuery tagSQuery = null;
        if (query.getTags() != null && !query.getTags().isEmpty()) {
            tagSQuery = new TermsQuery(IndexedDocumentModel.TAGS, query.getTags());
        }

        TermQuery starQuery = null;
        if (query.getStarred() != null) {
            starQuery = new TermQuery(IndexedDocumentModel.STARRED, query.getStarred());
        }

        RangeQuery amountQuery = null;
        if (query.getGreaterThan() != null || query.getLessThan() != null) {
            amountQuery = new RangeQuery(IndexedDocumentModel.AMOUNT);
            if (query.getGreaterThan() != null) {
                amountQuery.gte(query.getGreaterThan());
            }
            if (query.getLessThan() != null) {
                amountQuery.lte(query.getLessThan());
            }
        }

        RangeQuery issueDateQuery = null;
        if (query.getAfter() != null || query.getBefore() != null) {
            issueDateQuery = new RangeQuery(IndexedDocumentModel.ISSUE_DATE);
            if (query.getAfter() != null) {
                issueDateQuery.gte(query.getAfter());
            }
            if (query.getBefore() != null) {
                issueDateQuery.lte(query.getBefore());
            }
        }

        TermsQuery roleQuery = null;
        if (query.getRole() != null) {
            switch (query.getRole()) {
                case SENDER:
                    roleQuery = new TermsQuery(IndexedDocumentModel.SUPPLIER_ASSIGNED_ID, allPermittedSpaces.entrySet());
                    break;
                case RECEIVER:
                    roleQuery = new TermsQuery(IndexedDocumentModel.CUSTOMER_ASSIGNED_ID, allPermittedSpaces.entrySet());
                    break;
                default:
                    throw new IllegalStateException("Invalid role:" + query.getRole());
            }
        }

        String orderBy;
        if (query.getOrderBy() == null) {
            orderBy = IndexedDocumentModel.ISSUE_DATE;
        } else {
            orderBy = Stream.of(query.getOrderBy().split("(?<=[a-z])(?=[A-Z])")).collect(Collectors.joining("_")).toLowerCase();
        }

        IndexedDocumentQueryModel.Builder builder = IndexedDocumentQueryModel.builder()
                .filterText(query.getFilterText())
                .orderBy(orderBy, query.isAsc())
                .offset(query.getOffset() != null ? query.getOffset() : 0)
                .limit(query.getLimit() != null ? query.getLimit() : 10);

        if (typeQuery != null) {
            builder.addFilter(typeQuery);
        }
        if (currencyQuery != null) {
            builder.addFilter(currencyQuery);
        }
        if (tagSQuery != null) {
            builder.addFilter(tagSQuery);
        }
        if (starQuery != null) {
            builder.addFilter(starQuery);
        }
        if (amountQuery != null) {
            builder.addFilter(amountQuery);
        }
        if (issueDateQuery != null) {
            builder.addFilter(issueDateQuery);
        }
        if (roleQuery != null) {
            builder.addFilter(roleQuery);
        }

        SpaceModel[] spaces = allPermittedSpaces.values().toArray(new SpaceModel[allPermittedSpaces.size()]);
        SearchResultModel<IndexedDocumentModel> result = indexedDocumentProvider.getDocumentsUser(user, builder.build(), spaces);

        // Meta
        Map<String, Object> meta = new HashMap<>();
        meta.put("totalCount", result.getTotalResults());

        // Links
        Map<String, String> links = new HashMap<>();

        return Response.ok(new GenericDataRepresentation<>(result.getItems().stream()
                .map(indexedDocument -> modelToRepresentation.toRepresentation(user, indexedDocument.getDocument(), uriInfo))
                .collect(Collectors.toList()), links, meta)).build();
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
        List<InputPart> filename = formParts.get("filename");

        for (InputPart inputPart : inputParts) {
            // Extract file
            MultivaluedMap<String, String> headers = inputPart.getHeaders();
            String fileName = getFileName(headers);

            if (fileName == null) {
                throw new ErrorResponseException("Could not read filename", Response.Status.BAD_REQUEST);
            }

            InputStream inputStream;
            try {
                inputStream = inputPart.getBody(InputStream.class, null);
            } catch (IOException e) {
                throw new ErrorResponseException("Could not read file " + fileName, Response.Status.BAD_REQUEST);
            }

            // Save document
            try {
                importedDocumentManager.importDocument(inputStream, fileName, DocumentProviderType.USER_COLLECTOR);
            } catch (IOException e) {
                throw new ErrorResponseException("Could not read file", Response.Status.INTERNAL_SERVER_ERROR);
            } catch (NotReadableCompressFileException e) {
                throw new ErrorResponseException("Could not read file, corrupted file", Response.Status.BAD_REQUEST);
            }

            // Return result
            return Response.ok().build();
        }

        throw new ErrorResponseException("Could not find any file to process");
    }

    private String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                return name[1].trim().replaceAll("\"", "");
            }
        }
        return null;
    }

    @GET
    @Path("{documentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public DocumentRepresentation getDocument(@Context final HttpServletRequest httpServletRequest,
                                              @PathParam("documentId") String documentId) {
        UserModel user = getUser(httpServletRequest);
        DocumentModel document = getDocumentById(user, documentId);

        return modelToRepresentation.toRepresentation(user, document, uriInfo).toSpaceRepresentation();
    }

    @PATCH
    @Path("{documentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public DocumentRepresentation updateDocument(@Context final HttpServletRequest httpServletRequest,
                                                 @PathParam("documentId") String documentId,
                                                 DocumentRepresentation documentRepresentation) {
        UserModel user = getUser(httpServletRequest);
        DocumentModel document = getDocumentById(user, documentId);
        IndexedDocumentModel indexedDocument = document.getIndexedDocument();

        DocumentRepresentation.Data data = documentRepresentation.getData();
        updateDocument(data.getAttributes(), user, document);

        return modelToRepresentation.toRepresentation(user, document, uriInfo).toSpaceRepresentation();
    }

    @PATCH
    @Path("massive")
    @Produces(MediaType.APPLICATION_JSON)
    public void updateDocuments(@Context final HttpServletRequest httpServletRequest,
                                GenericDataRepresentation<List<DocumentRepresentation.Data>> representation) {
        UserModel user = getUser(httpServletRequest);

        representation.getData().forEach(documentRepresentation -> {
            DocumentModel document = getDocumentById(user, documentRepresentation.getId());
            updateDocument(documentRepresentation.getAttributes(), user, document);
        });
    }

    private void updateDocument(DocumentRepresentation.Attributes attributes, UserModel user, DocumentModel document) {
        IndexedDocumentModel indexedDocument = document.getIndexedDocument();

        if (attributes.getViewed() != null && attributes.getViewed()) {
            indexedDocument.addViewed(user.getId());
        }
        if (attributes.getStarred() != null && attributes.getStarred()) {
            indexedDocument.addStart(user.getId());
        }
        if (attributes.getChecked() != null && attributes.getChecked()) {
            indexedDocument.addCheck(user.getId());
        }
    }

    @GET
    @Path("/massive/download")
    @Produces("application/zip")
    public Response downloadDocuments(@Context final HttpServletRequest httpServletRequest,
                                      @QueryParam("documents") List<String> documentsId) {
        UserModel user = getUser(httpServletRequest);

        ZipBuilder zipInMemory = ZipBuilder.createZipInMemory();
        documentsId.stream()
                .map(documentId -> getDocumentById(user, documentId))
                .forEach(document -> {
                    FileModel file = document.getCurrentVersion().getImportedDocument().getFile();
                    try {
                        zipInMemory.add(file.getFileAsBytes()).path(document.getAssignedId()).save();
                    } catch (IOException e) {
                        logger.error("Could not add file to zip", e);
                    }
                });

        byte[] zip = zipInMemory.toBytes();

        Response.ResponseBuilder response = Response.ok(zip);
        response.header("Content-Disposition", "attachment; filename=\"" + "files" + ".zip\"");
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
        UserModel user = getUser(httpServletRequest);

        //
        ExportFormat exportFormat = ExportFormat.valueOf(format.toUpperCase());

        Set<DocumentModel> documents = documentsId.stream()
                .map(documentId -> getDocumentById(user, documentId))
                .collect(Collectors.toSet());

        ReportTemplateConfiguration reportConfig = ReportTemplateConfiguration.builder()
                .themeName(theme)
                .locale(user.getDefaultLanguage() != null ? new Locale(user.getDefaultLanguage()) : Locale.ENGLISH)
                .build();

        ZipBuilder zipInMemory = ZipBuilder.createZipInMemory();
        for (DocumentModel document : documents) {
            try {
                byte[] bytes = reportTemplateProvider.getReport(reportConfig, document, exportFormat);
                zipInMemory.add(bytes).path(document.getAssignedId() + "." + exportFormat.getExtension()).save();
            } catch (ReportException e) {
                logger.error("Could not generate a report", e);
            } catch (IOException e) {
                logger.error("Could not add file to zip", e);
            }
        }
        byte[] zip = zipInMemory.toBytes();

        Response.ResponseBuilder response = Response.ok(zip);
        response.header("Content-Disposition", "attachment; filename=\"" + "file" + ".zip\"");
        return response.build();
    }

    @GET
    @Path("/{documentId}/download")
    @Produces("application/xml")
    public Response getXml(@Context final HttpServletRequest httpServletRequest,
                           @PathParam("documentId") String documentId) {
        UserModel user = getUser(httpServletRequest);
        DocumentModel document = getDocumentById(user, documentId);

        FileModel file = document.getCurrentVersion().getImportedDocument().getFile();

        byte[] reportBytes = file.getFileAsBytes();

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
        UserModel user = getUser(httpServletRequest);
        DocumentModel document = getDocumentById(user, documentId);

        //
        ExportFormat exportFormat = ExportFormat.valueOf(format.toUpperCase());
        ReportTemplateConfiguration reportConfig = ReportTemplateConfiguration.builder()
                .themeName(theme)
                .locale(user.getDefaultLanguage() != null ? new Locale(user.getDefaultLanguage()) : Locale.ENGLISH)
                .build();

        byte[] reportBytes;
        try {
            reportBytes = reportTemplateProvider.getReport(reportConfig, document, exportFormat);
        } catch (ReportException e) {
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
