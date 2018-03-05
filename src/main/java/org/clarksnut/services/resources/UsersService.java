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
import org.clarksnut.representations.idm.*;
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
@Api(value = "Users", description = "Users REST API", consumes = "application/json")
public class UsersService extends AbstractResource {

    private static final Logger logger = Logger.getLogger(UsersService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private ReportTemplateProvider reportTemplateProvider;

    @Inject
    private RequestProvider requestProvider;

    @Inject
    private PartyProvider partyProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Users")
    public GenericDataRepresentation<List<UserRepresentation.UserData>> getUsers(
            @ApiParam(value = "Username") @QueryParam("username") String username,
            @ApiParam(value = "Filter Text") @QueryParam("filterText") String filterText,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Max results") @QueryParam("limit") @DefaultValue("10") int limit
    ) {
        List<UserRepresentation.UserData> data;

        if (username != null) {
            UserModel user = userProvider.getUserByUsername(username);
            if (user == null) {
                data = Collections.emptyList();
            } else {
                data = Collections.singletonList(modelToRepresentation.toRepresentation(user, uriInfo, false));
            }
        } else {
            Optional<String> filterTextOptional = Optional.ofNullable(filterText);
            data = userProvider.getUsers(filterTextOptional.orElse(""), offset, limit)
                    .stream()
                    .map(user -> modelToRepresentation.toRepresentation(user, uriInfo, false))
                    .collect(Collectors.toList());
        }

        return new GenericDataRepresentation<>(data);
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get User")
    public UserRepresentation getUser(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @Context final HttpServletRequest request
    ) {
        UserModel user;
        boolean fullInfo = false;

        if (userId.equals("me")) {
            user = getUserSession(request);
            fullInfo = true;
        } else {
            user = getUserById(userId);
        }

        return modelToRepresentation.toRepresentation(user, uriInfo, fullInfo).toUserRepresentation();
    }

    @PUT
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update User")
    public UserRepresentation updateUser(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @Context final HttpServletRequest request,
            final UserRepresentation userRepresentation
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        UserRepresentation.UserAttributesRepresentation attributes = userRepresentation.getData().getAttributes();

        if (attributes != null) {
            // Is registration completed
            Boolean registrationCompleted = attributes.getRegistrationCompleted();
            if (registrationCompleted != null) {
                user.setRegistrationCompleted(registrationCompleted);
            }

            // Profile
            if (attributes.getFullName() != null) {
                user.setFullName(attributes.getFullName());
            }
            if (attributes.getCompany() != null) {
                user.setCompany(attributes.getCompany());
            }
            if (attributes.getImageURL() != null) {
                user.setImageURL(attributes.getImageURL());
            }
            if (attributes.getUrl() != null) {
                user.setUrl(attributes.getUrl());
            }
            if (attributes.getBio() != null) {
                user.setBio(attributes.getBio());
            }

            if (attributes.getDefaultLanguage() != null) {
                user.setDefaultLanguage(attributes.getDefaultLanguage());
            }
        }

        return modelToRepresentation.toRepresentation(user, uriInfo, true).toUserRepresentation();
    }

    /**
     * Spaces
     */

    @GET
    @Path("/{userId}/spaces")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return allowed Spaces of User")
    public GenericDataRepresentation<List<SpaceRepresentation.SpaceData>> getUserSpaces(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Role", allowableValues = "owner, collaborator") @QueryParam("role") @DefaultValue("owner") String role,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Max results") @QueryParam("limit") @DefaultValue("10") int limit,
            @Context final HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;
        boolean fullInfo = false;

        if (userId.equals("me")) {
            user = getUserSession(request);
            fullInfo = true;
        } else {
            user = getUserById(userId);
        }

        int totalCount;
        List<SpaceModel> spaces;

        // Search
        PermissionType permissionType = PermissionType.valueOf(role.toUpperCase());
        switch (permissionType) {
            case OWNER:
                spaces = user.getOwnedSpaces(offset, limit + 1);
                totalCount = user.getOwnedSpaces().size();
                break;
            case COLLABORATOR:
                spaces = user.getCollaboratedSpaces(offset, limit + 1);
                totalCount = user.getCollaboratedSpaces().size();
                break;
            default:
                throw new ErrorResponseException("Invalid Role", Response.Status.BAD_REQUEST);
        }

        // Metadata
        Map<String, Object> meta = new HashMap<>();
        meta.put("totalCount", totalCount);

        // Links
        Map<String, String> links = new HashMap<>();
        links.put("first", uriInfo.getBaseUriBuilder()
                .path(UsersService.class)
                .path(UsersService.class, "getUserSpaces")
                .build(userId).toString() +
                "?role=" + role +
                "?offset=0" +
                "&limit=" + limit);

        links.put("last", uriInfo.getBaseUriBuilder()
                .path(UsersService.class)
                .path(UsersService.class, "getUserSpaces")
                .build(userId).toString() +
                "?role=" + role +
                "?offset=" + (totalCount > 0 ? (((totalCount - 1) % limit) * limit) : 0) +
                "&limit=" + limit);

        if (spaces.size() > limit) {
            links.put("next", uriInfo.getBaseUriBuilder()
                    .path(UsersService.class)
                    .path(UsersService.class, "getUserSpaces")
                    .build(userId).toString() +
                    "?role=" + role +
                    "?offset=" + (offset + limit) +
                    "&limit=" + limit);

            // Remove last item
            spaces.remove(spaces.size() - 1);
        }


        final boolean isFullInfo = fullInfo;
        List<SpaceRepresentation.SpaceData> spacesData = spaces.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo, isFullInfo))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(spacesData, links, meta);
    }

    @GET
    @Path("/{userId}/spaces/{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Space")
    public SpaceRepresentation getUserSpace(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;
        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED); // Use /spaces/{spaceId} instead
        }

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        boolean fullInfo = false;
        if (user.equals(spaceOwner)) {
            fullInfo = true;
        }

        return modelToRepresentation.toRepresentation(space, uriInfo, fullInfo).toSpaceRepresentation();
    }

    @PUT
    @Path("/{userId}/spaces/{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update space")
    public SpaceRepresentation updateUserSpace(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest httpServletRequest,
            final SpaceRepresentation spaceRepresentation
    ) throws ErrorResponseException {
        UserModel user;
        if (userId.equals("me")) {
            user = getUserSession(httpServletRequest);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        if (!user.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        SpaceRepresentation.SpaceData data = spaceRepresentation.getData();
        SpaceRepresentation.SpaceAttributes attributes = data.getAttributes();
        if (attributes.getName() != null) {
            space.setName(attributes.getName());
        }
        if (attributes.getDescription() != null) {
            space.setDescription(attributes.getDescription());
        }

        return modelToRepresentation.toRepresentation(space, uriInfo, true).toSpaceRepresentation();
    }

    @DELETE
    @Path("/{userId}/spaces/{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Delete space")
    public void deleteUserSpace(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        if (!user.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        spaceProvider.removeSpace(space);
    }

    /**
     * Space Collaborators
     */

    @GET
    @Path("/{userId}/spaces/{spaceId}/collaborators")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Collaborators")
    public GenericDataRepresentation<List<UserRepresentation.UserData>> getUserSpaceCollaborators(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Max results") @QueryParam("limit") @DefaultValue("10") int limit,
            @Context HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        if (!user.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        List<UserModel> collaborators = space.getCollaborators(offset, limit + 1);
        int totalCount = space.getCollaborators().size();

        // Metadata
        Map<String, Object> meta = new HashMap<>();
        meta.put("totalCount", totalCount);

        // Links
        Map<String, String> links = new HashMap<>();
        links.put("first", uriInfo.getBaseUriBuilder()
                .path(SpacesService.class)
                .path(SpacesService.class, "getUserSpaceCollaborators")
                .build(spaceId).toString() +
                "?offset=0" +
                "&limit=" + limit);

        links.put("last", uriInfo.getBaseUriBuilder()
                .path(SpacesService.class)
                .path(SpacesService.class, "getUserSpaceCollaborators")
                .build(spaceId).toString() +
                "?offset=" + (totalCount > 0 ? (((totalCount - 1) % limit) * limit) : 0) +
                "&limit=" + limit);

        if (collaborators.size() > limit) {
            links.put("next", uriInfo.getBaseUriBuilder()
                    .path(SpacesService.class)
                    .path(SpacesService.class, "getUserSpaceCollaborators")
                    .build(spaceId).toString() +
                    "?offset=" + (offset + limit) +
                    "&limit=" + limit);

            // Remove last item
            collaborators.remove(collaborators.size() - 1);
        }

        return new GenericDataRepresentation<>(collaborators.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo, false))
                .collect(Collectors.toList()), links, meta);
    }

    @POST
    @Path("/{userId}/spaces/{spaceId}/collaborators")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Add new Collaborator")
    public void addSpaceCollaborators(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest request,
            final TypedGenericDataRepresentation<List<UserRepresentation.UserData>> representation
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();
        List<UserModel> currentCollaborators = space.getCollaborators();

        if (!user.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        for (UserRepresentation.UserData data : representation.getData()) {
            UserModel newCollaborator = userProvider.getUserByUsername(data.getAttributes().getUsername());
            if (!currentCollaborators.contains(newCollaborator)) {
                space.addCollaborators(newCollaborator);
            } else {
                throw new ErrorResponseException("Collaborator already registered", Response.Status.BAD_REQUEST);
            }
        }
    }

    @DELETE
    @Path("/{userId}/spaces/{spaceId}/collaborators/{collaboratorId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Remove Collaborator")
    public Response removeSpaceCollaborators(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @ApiParam(value = "User Id") @PathParam("collaboratorId") String collaboratorId,
            @Context HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();
        List<UserModel> collaborators = space.getCollaborators();

        if (!user.equals(spaceOwner) && !collaborators.contains(user)) {
            throw new ForbiddenException();
        }


        UserModel collaborator = userProvider.getUser(collaboratorId);
        if (spaceOwner.equals(collaborator)) {
            return ErrorResponse.error("Could not delete the owner", Response.Status.BAD_REQUEST);
        }
        space.removeCollaborators(collaborator);
        return Response.ok().build();
    }

    /*
     * Space request access
     */

    @GET
    @Path("/{userId}/spaces/{spaceId}/request-access")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Notifications")
    public GenericDataRepresentation<List<RequestRepresentation.RequestData>> getUserSpaceRequestAccess(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @ApiParam(value = "Status", allowableValues = "pending, accepted, rejected") @DefaultValue("pending") @QueryParam("status") String status,
            @Context final HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();
        if (!user.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        RequestStatus requestStatus = RequestStatus.valueOf(status.toUpperCase());

        List<RequestRepresentation.RequestData> requests = requestProvider.getRequests(requestStatus, new SpaceModel[]{space})
                .stream()
                .map(f -> modelToRepresentation.toRepresentation(f))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(requests);
    }

    /**
     * Documents
     */

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

        List<DocumentRepresentation.DocumentData> documents = documentProvider
                .getDocuments(filterText, offset, limit, spaces.toArray(new SpaceModel[spaces.size()]))
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

    /*
     * Parties
     * */

    @GET
    @Path("/{userId}/parties")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get parties")
    public GenericDataRepresentation<List<PartyRepresentation.PartyData>> getParties(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Filter Text") @QueryParam("filterText") String filterText,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Max result") @QueryParam("limit") @DefaultValue("10") int limit,
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

        List<PartyRepresentation.PartyData> parties = partyProvider.getParties(filterText, limit, spaces.toArray(new SpaceModel[spaces.size()]))
                .stream()
                .map(party -> modelToRepresentation.toRepresentation(party))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(parties);
    }

    /*
     * Notifications
     * */

    @GET
    @Path("/{userId}/notifications")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Notifications")
    public NotificationsRepresentation getNotifications(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Ids") @QueryParam("space") List<String> spaceIds,
            @ApiParam(value = "Status", allowableValues = "pending, accepted, rejected") @DefaultValue("pending") @QueryParam("status") String status,
            @Context final HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;
        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        Set<SpaceModel> spaces = filterAllowedSpaces(user, spaceIds);
        RequestStatus requestStatus = RequestStatus.valueOf(status.toUpperCase());

        List<RequestRepresentation.RequestData> requests = requestProvider.getRequests(requestStatus, spaces.toArray(new SpaceModel[spaces.size()]))
                .stream()
                .map(f -> modelToRepresentation.toRepresentation(f))
                .collect(Collectors.toList());

        // Representation
        NotificationsRepresentation.NotificationsData data = new NotificationsRepresentation.NotificationsData();
        data.setType(ModelType.NOTIFICATIONS.getAlias());

        NotificationsRepresentation.NotificationsAttributes attributes = new NotificationsRepresentation.NotificationsAttributes();
        data.setAttributes(attributes);

        attributes.setRequestAccesses(requests);
        return data.toNotificationsRepresentation();
    }
}