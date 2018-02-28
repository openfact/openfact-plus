package org.clarksnut.services.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.clarksnut.files.FileModel;
import org.clarksnut.files.uncompress.exceptions.NotReadableCompressFileException;
import org.clarksnut.managers.ImportedDocumentManager;
import org.clarksnut.models.*;
import org.clarksnut.models.exceptions.IsNotXmlOrCompressedFileDocumentException;
import org.clarksnut.query.RangeQuery;
import org.clarksnut.query.TermQuery;
import org.clarksnut.query.TermsQuery;
import org.clarksnut.report.ExportFormat;
import org.clarksnut.report.ReportTemplateConfiguration;
import org.clarksnut.report.ReportTemplateProvider;
import org.clarksnut.report.exceptions.ReportException;
import org.clarksnut.representations.idm.*;
import org.clarksnut.services.ErrorResponse;
import org.clarksnut.services.ErrorResponseException;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

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
@Path("/api/documents")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Documents", consumes = "application/json")
public class DocumentsService extends AbstractResource {

    private static final Logger logger = Logger.getLogger(DocumentsService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private ImportedDocumentManager importedDocumentManager;

    @Inject
    private DocumentProvider documentProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @Inject
    private ReportTemplateProvider reportTemplateProvider;

    private DocumentModel getDocumentById(UserModel user, String documentId) {
        DocumentModel document = documentProvider.getDocument(documentId);
        if (document == null) {
            throw new NotFoundException();
        }
        return document;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Documents", notes = "This will search just on Owned and Collaborated Spaces")
    public GenericDataRepresentation<List<DocumentRepresentation.DocumentData>> getDocuments(
            @ApiParam(value = "A text for filter results") @QueryParam("q") String searchText,
            @ApiParam(value = "The first position of array results") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "The max number of results") @QueryParam("limit") @DefaultValue("10") int limit,
            @ApiParam(value = "List of space ids to search in. If null or empty all allowed spaces of user will be used") @QueryParam("space") List<String> spaceIds,
            @Context HttpServletRequest httpServletRequest) throws ErrorResponseException {
        UserModel sessionUser = getUserSession(httpServletRequest);
        Set<SpaceModel> spaces = filterAllowedSpaces(sessionUser, spaceIds);

        List<DocumentRepresentation.DocumentData> documents = documentProvider
                .getDocuments(searchText, offset, limit, spaces.toArray(new SpaceModel[spaces.size()]))
                .stream()
                .map(document -> modelToRepresentation.toRepresentation(sessionUser, document, uriInfo))
                .collect(Collectors.toList());

        return new GenericDataRepresentation<>(documents);
    }

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Search Document", notes = "This will search document in advanced mode")
    public GenericDataRepresentation<List<DocumentRepresentation.DocumentData>> searchDocuments(
            DocumentQueryRepresentation query,
            @Context HttpServletRequest httpServletRequest) throws ErrorResponseException {
        UserModel sessionUser = getUserSession(httpServletRequest);

        DocumentQueryRepresentation.DocumentQueryData queryData = query.getData();
        DocumentQueryRepresentation.DocumentQueryAttributes queryAttributes = queryData.getAttributes();
        Set<SpaceModel> spaces = filterAllowedSpaces(sessionUser, queryData.getAttributes().getSpaces());


        // Search query
        TermsQuery typeQuery = null;
        if (queryAttributes.getTypes() != null && !queryAttributes.getTypes().isEmpty()) {
            typeQuery = new TermsQuery(DocumentModel.TYPE, queryAttributes.getTypes());
        }

        TermsQuery currencyQuery = null;
        if (queryAttributes.getCurrencies() != null && !queryAttributes.getCurrencies().isEmpty()) {
            currencyQuery = new TermsQuery(DocumentModel.CURRENCY, queryAttributes.getCurrencies());
        }

        TermsQuery tagSQuery = null;
        if (queryAttributes.getTags() != null && !queryAttributes.getTags().isEmpty()) {
            tagSQuery = new TermsQuery(DocumentModel.TAGS, queryAttributes.getTags());
        }

        TermQuery starQuery = null;
        if (queryAttributes.getStarred() != null) {
            starQuery = new TermQuery(DocumentModel.STARRED, queryAttributes.getStarred());
        }

        RangeQuery amountQuery = null;
        if (queryAttributes.getGreaterThan() != null || queryAttributes.getLessThan() != null) {
            amountQuery = new RangeQuery(DocumentModel.AMOUNT);
            if (queryAttributes.getGreaterThan() != null) {
                amountQuery.gte(queryAttributes.getGreaterThan());
            }
            if (queryAttributes.getLessThan() != null) {
                amountQuery.lte(queryAttributes.getLessThan());
            }
        }

        RangeQuery issueDateQuery = null;
        if (queryAttributes.getAfter() != null || queryAttributes.getBefore() != null) {
            issueDateQuery = new RangeQuery(DocumentModel.ISSUE_DATE);
            if (queryAttributes.getAfter() != null) {
                issueDateQuery.gte(queryAttributes.getAfter());
            }
            if (queryAttributes.getBefore() != null) {
                issueDateQuery.lte(queryAttributes.getBefore());
            }
        }

        TermsQuery roleQuery = null;
        if (queryAttributes.getRole() != null) {
            List<String> spaceIds = spaces.stream().map(SpaceModel::getAssignedId).collect(Collectors.toList());
            switch (queryAttributes.getRole()) {
                case SENDER:
                    roleQuery = new TermsQuery(DocumentModel.SUPPLIER_ASSIGNED_ID, spaceIds);
                    break;
                case RECEIVER:
                    roleQuery = new TermsQuery(DocumentModel.CUSTOMER_ASSIGNED_ID, spaceIds);
                    break;
                default:
                    throw new IllegalStateException("Invalid role:" + queryAttributes.getRole());
            }
        }

        String orderBy = queryAttributes.getOrderBy();
        if (queryAttributes.getOrderBy() == null) {
            orderBy = DocumentModel.ISSUE_DATE;
        }

        DocumentQueryModel.Builder builder = DocumentQueryModel.builder()
                .filterText(queryAttributes.getFilterText())
                .orderBy(orderBy, queryAttributes.isAsc())
                .offset(queryAttributes.getOffset() != null ? queryAttributes.getOffset() : 0)
                .limit(queryAttributes.getLimit() != null ? queryAttributes.getLimit() : 10);

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

        SearchResultModel<DocumentModel> result = documentProvider.searchDocuments(builder.build(), spaces.toArray(new SpaceModel[spaces.size()]));

        // Meta
        Map<String, Object> meta = new HashMap<>();
        meta.put("totalCount", result.getTotalResults());

        // Facets
        Map<String, List<FacetRepresentation>> facets = new HashMap<>();
        for (Map.Entry<String, List<FacetModel>> entry : result.getFacets().entrySet()) {
            facets.put(entry.getKey(), entry.getValue().stream().map(f -> modelToRepresentation.toRepresentation(f)).collect(Collectors.toList()));
        }
        meta.put("facets", facets);

        // Links
        Map<String, String> links = new HashMap<>();

        return new GenericDataRepresentation<>(result.getItems().stream()
                .map(document -> modelToRepresentation.toRepresentation(sessionUser, document, uriInfo))
                .collect(Collectors.toList()), links, meta);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Import Document", notes = "This will import xml or compressed files")
    public Response importDocument(final MultipartFormDataInput multipartFormDataInput) throws ErrorResponseException {
        Map<String, List<InputPart>> formParts = multipartFormDataInput.getFormDataMap();
        List<InputPart> inputParts = formParts.get("file");

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
                logger.debug("Document has been imported");
            } catch (IOException e) {
                throw new ErrorResponseException("Could not read file", Response.Status.INTERNAL_SERVER_ERROR);
            } catch (IsNotXmlOrCompressedFileDocumentException e) {
                throw new ErrorResponseException("File should be .xml or compressed e.x. .zip, .tag.gz, .rar", Response.Status.BAD_REQUEST);
            } catch (NotReadableCompressFileException e) {
                throw new ErrorResponseException("Could not uncompress file, corrupted file", Response.Status.BAD_REQUEST);
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
    @ApiOperation(value = "Get Document", notes = "This will return a document")
    public DocumentRepresentation getDocument(
            @ApiParam(value = "Document Id") @PathParam("documentId") String documentId,
            @Context final HttpServletRequest httpServletRequest) {
        UserModel sessionUser = getUserSession(httpServletRequest);
        DocumentModel document = getDocumentById(sessionUser, documentId);
        if (isUserAllowedToViewDocument(sessionUser, document)) {
            return modelToRepresentation.toRepresentation(sessionUser, document, uriInfo).toSpaceRepresentation();
        } else {
            throw new ForbiddenException();
        }
    }

    @PUT
    @Path("{documentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update Document", notes = "This will update the document just for current user")
    public DocumentRepresentation updateDocument(
            @ApiParam(value = "Document Id") @PathParam("documentId") String documentId,
            @Context final HttpServletRequest httpServletRequest,
            DocumentRepresentation documentRepresentation) {
        UserModel sessionUser = getUserSession(httpServletRequest);
        DocumentModel document = getDocumentById(sessionUser, documentId);
        if (!isUserAllowedToViewDocument(sessionUser, document)) {
            throw new ForbiddenException();
        }

        DocumentRepresentation.DocumentData data = documentRepresentation.getData();
        updateDocument(data.getAttributes(), sessionUser, document);
        return modelToRepresentation.toRepresentation(sessionUser, document, uriInfo).toSpaceRepresentation();
    }


    private void updateDocument(DocumentRepresentation.DocumentAttributes attributes, UserModel user, DocumentModel document) {
        if (attributes.getViewed() != null) {
            if (attributes.getViewed()) document.addViewed(user.getId());
            else document.removeViewed(user.getId());
        }
        if (attributes.getStarred() != null) {
            if (attributes.getStarred()) document.addStart(user.getId());
            else document.removeStart(user.getId());
        }
        if (attributes.getChecked() != null) {
            if (attributes.getChecked()) document.addCheck(user.getId());
            else document.removeCheck(user.getId());
        }
    }

    @GET
    @Path("/{documentId}/download")
    @Produces("application/xml")
    @ApiOperation(value = "Download Document", notes = "This will download the document")
    public Response getXml(
            @ApiParam(value = "Document Id") @PathParam("documentId") String documentId,
            @Context final HttpServletRequest httpServletRequest) {
        UserModel sessionUser = getUserSession(httpServletRequest);
        DocumentModel document = getDocumentById(sessionUser, documentId);
        if (!isUserAllowedToViewDocument(sessionUser, document)) {
            throw new ForbiddenException();
        }

        FileModel file = document.getCurrentVersion().getImportedDocument().getFile();
        byte[] reportBytes = file.getFile();

        Response.ResponseBuilder response = Response.ok(reportBytes);
        response.header("Content-Disposition", "attachment; filename=\"" + document.getAssignedId() + ".xml\"");
        return response.build();
    }

    @GET
    @Path("/{documentId}/print")
    @ApiOperation(value = "Print Document", notes = "This will print the document")
    public Response printDocument(
            @Context final HttpServletRequest httpServletRequest,
            @ApiParam(value = "Document Id") @PathParam("documentId") String documentId,
            @ApiParam(value = "Theme") @QueryParam("theme") String theme,
            @ApiParam(value = "format", allowableValues = "pdf, html") @QueryParam("format") @DefaultValue("pdf") String format) {
        UserModel sessionUser = getUserSession(httpServletRequest);
        DocumentModel document = getDocumentById(sessionUser, documentId);
        if (!isUserAllowedToViewDocument(sessionUser, document)) {
            throw new ForbiddenException();
        }


        ExportFormat exportFormat = ExportFormat.valueOf(format.toUpperCase());
        ReportTemplateConfiguration reportConfig = ReportTemplateConfiguration.builder()
                .themeName(theme)
                .locale(sessionUser.getDefaultLanguage() != null ? new Locale(sessionUser.getDefaultLanguage()) : Locale.ENGLISH)
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
