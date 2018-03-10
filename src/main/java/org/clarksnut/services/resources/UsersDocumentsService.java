package org.clarksnut.services.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.clarksnut.files.FileModel;
import org.clarksnut.models.*;
import org.clarksnut.query.RangeQuery;
import org.clarksnut.query.TermQuery;
import org.clarksnut.query.TermsQuery;
import org.clarksnut.report.ExportFormat;
import org.clarksnut.report.ReportTemplateConfiguration;
import org.clarksnut.report.ReportTemplateProvider;
import org.clarksnut.report.exceptions.ReportException;
import org.clarksnut.representations.idm.DocumentQueryRepresentation;
import org.clarksnut.representations.idm.DocumentRepresentation;
import org.clarksnut.representations.idm.FacetRepresentation;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.services.ErrorResponse;
import org.clarksnut.services.ErrorResponseException;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Path("/api/users")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Users", description = "Users Documents REST API", consumes = "application/json")
public class UsersDocumentsService extends AbstractResource {

    private static final Logger logger = Logger.getLogger(UsersDocumentsService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private ReportTemplateProvider reportTemplateProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @GET
    @Path("/{userId}/documents")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Documents")
    public GenericDataRepresentation<List<DocumentRepresentation.DocumentData>> getUserDocuments(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Filter text") @QueryParam("filterText") String filterText,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Maz results") @QueryParam("limit") @DefaultValue("10") int limit,
            @ApiParam(value = "Space Ids") @QueryParam("space") List<String> spaceIds,
            @Context HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        Set<SpaceModel> spaces = filterAllowedSpaces(user, spaceIds);

        Optional<String> optionalFilterText = Optional.ofNullable(filterText);
        List<DocumentRepresentation.DocumentData> documents = documentProvider
                .getDocuments(optionalFilterText.orElse(""), offset, limit, spaces.toArray(new SpaceModel[spaces.size()]))
                .stream()
                .map(document -> modelToRepresentation.toRepresentation(user, document, uriInfo))
                .collect(Collectors.toList());

        return new GenericDataRepresentation<>(documents);
    }

    @POST
    @Path("/{userId}/documents/search")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Search Document", notes = "Search on allowed user (session) spaces")
    public GenericDataRepresentation<List<DocumentRepresentation.DocumentData>> searchUserDocuments(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @Context HttpServletRequest request,
            DocumentQueryRepresentation query
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        DocumentQueryRepresentation.DocumentQueryData queryData = query.getData();
        DocumentQueryRepresentation.DocumentQueryAttributes queryAttributes = queryData.getAttributes();
        Set<SpaceModel> spaces = filterAllowedSpaces(user, queryData.getAttributes().getSpaces());


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
            starQuery = new TermQuery(DocumentModel.STARRED, user.getId());
        }

        TermQuery viewQuery = null;
        if (queryAttributes.getViewed() != null) {
            viewQuery = new TermQuery(DocumentModel.VIEWED, user.getId());
        }

        TermQuery checkQuery = null;
        if (queryAttributes.getChecked() != null) {
            checkQuery = new TermQuery(DocumentModel.CHECKED, user.getId());
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

        Optional<String> optionalFilterText = Optional.ofNullable(queryAttributes.getFilterText());
        DocumentQueryModel.Builder builder = DocumentQueryModel.builder()
                .filterText(optionalFilterText.orElse(""))
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
            if (queryAttributes.getStarred()) {
                builder.addFilter(starQuery);
            } else {
                builder.addNegativeFilter(starQuery);
            }
        }
        if (viewQuery != null) {
            if (queryAttributes.getViewed()) {
                builder.addFilter(viewQuery);
            } else {
                builder.addNegativeFilter(viewQuery);
            }
        }
        if (checkQuery != null) {
            if (queryAttributes.getChecked()) {
                builder.addFilter(checkQuery);
            } else {
                builder.addNegativeFilter(checkQuery);
            }
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
                .map(document -> modelToRepresentation.toRepresentation(user, document, uriInfo))
                .collect(Collectors.toList()), links, meta);
    }

    @GET
    @Path("/{userId}/documents/{documentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Document")
    public DocumentRepresentation getUserDocument(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Document Id") @PathParam("documentId") String documentId,
            @Context final HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED); // use /documents/{documentId} instead
        }

        DocumentModel document = getDocumentById(documentId);
        if (isUserAllowedToViewDocument(user, document)) {
            return modelToRepresentation.toRepresentation(user, document, uriInfo).toSpaceRepresentation();
        } else {
            throw new ForbiddenException();
        }
    }

    @PUT
    @Path("/{userId}/documents/{documentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update a Document")
    public DocumentRepresentation updateUserDocument(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Document Id") @PathParam("documentId") String documentId,
            @Context final HttpServletRequest request,
            DocumentRepresentation documentRepresentation
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        DocumentModel document = getDocumentById(documentId);
        if (!isUserAllowedToViewDocument(user, document)) {
            throw new ForbiddenException();
        }

        DocumentRepresentation.DocumentData data = documentRepresentation.getData();
        updateDocument(data.getAttributes(), user, document);
        return modelToRepresentation.toRepresentation(user, document, uriInfo).toSpaceRepresentation();
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
    @Path("/{userId}/documents/{documentId}/download")
    @Produces("application/xml")
    @ApiOperation(value = "Download Document")
    public Response downloadUserDocumentXml(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Document Id") @PathParam("documentId") String documentId,
            @Context final HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED); // use /documents/{documentId}/download instead
        }

        DocumentModel document = getDocumentById(documentId);
        if (!isUserAllowedToViewDocument(user, document)) {
            throw new ForbiddenException();
        }

        FileModel file = document.getCurrentVersion().getImportedDocument().getFile();
        byte[] reportBytes = file.getFile();

        Response.ResponseBuilder response = Response.ok(reportBytes);
        response.header("Content-Disposition", "attachment; filename=\"" + document.getAssignedId() + ".xml\"");
        return response.build();
    }

    @GET
    @Path("/{userId}/documents/{documentId}/print")
    @ApiOperation(value = "Print Document")
    public Response downloadPdf(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Document Id") @PathParam("documentId") String documentId,
            @ApiParam(value = "Theme") @QueryParam("theme") String theme,
            @ApiParam(value = "format", allowableValues = "pdf, html") @QueryParam("format") @DefaultValue("pdf") String format,
            @Context final HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;
        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED); // use /documents/{documentId}/print instead
        }

        DocumentModel document = getDocumentById(documentId);
        if (!isUserAllowedToViewDocument(user, document)) {
            throw new ForbiddenException();
        }


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