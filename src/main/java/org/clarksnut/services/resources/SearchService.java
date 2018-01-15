package org.clarksnut.services.resources;

import org.clarksnut.documents.IndexedDocumentModel;
import org.clarksnut.documents.IndexedDocumentProvider;
import org.clarksnut.documents.IndexedDocumentQueryModel;
import org.clarksnut.documents.SearchResultModel;
import org.clarksnut.models.*;
import org.clarksnut.query.*;
import org.clarksnut.representations.idm.DocumentQueryRepresentation;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.representations.idm.SpaceRepresentation;
import org.clarksnut.representations.idm.UserRepresentation;
import org.clarksnut.services.ErrorResponse;
import org.clarksnut.services.ErrorResponseException;
import org.clarksnut.utils.ModelToRepresentation;
import org.keycloak.KeycloakPrincipal;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
@Path("search")
@Consumes(MediaType.APPLICATION_JSON)
public class SearchService {

    @Context
    private UriInfo uriInfo;

    @Inject
    private SpaceProvider spaceProvider;

    @Inject
    private UserProvider userProvider;

    @Inject
    private IndexedDocumentProvider indexedDocumentProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    private UserModel getUser(HttpServletRequest httpServletRequest) {
        KeycloakPrincipal principal = (KeycloakPrincipal) httpServletRequest.getUserPrincipal();
        String username = principal.getKeycloakSecurityContext().getIdToken().getPreferredUsername();

        UserModel user = userProvider.getUserByUsername(username);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    @GET
    @Path("spaces")
    public GenericDataRepresentation searchSpaces(@QueryParam("q") String filterText) {
        QueryModel.Builder queryBuilder = QueryModel.builder();

        if (filterText != null) {
            queryBuilder.filterText(filterText);
        }

        List<SpaceRepresentation.Data> data = spaceProvider.getSpaces(queryBuilder.build()).stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(data);
    }

    @GET
    @Path("users")
    public GenericDataRepresentation searchUsers(@QueryParam("q") String filterText) {
        QueryModel.Builder queryBuilder = QueryModel.builder();

        if (filterText != null) {
            queryBuilder.filterText(filterText);
        }

        List<UserRepresentation.Data> data = userProvider.getUsers(queryBuilder.build()).stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(data);
    }

    @GET
    @Path("documents")
    public Response searchDocuments(@Context HttpServletRequest httpServletRequest,
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

        Set<SpaceModel> ownedSpaces = user.getOwnedSpaces();
        Set<SpaceModel> collaboratedSpaces = user.getCollaboratedSpaces();

        Set<String> allSpaces = new HashSet<>();
        allSpaces.addAll(ownedSpaces.stream().map(SpaceModel::getAssignedId).collect(Collectors.toSet()));
        allSpaces.addAll(collaboratedSpaces.stream().map(SpaceModel::getAssignedId).collect(Collectors.toSet()));
        if (query.getSpaces() != null && !query.getSpaces().isEmpty()) {
            allSpaces.retainAll(query.getSpaces());
        }
        if (allSpaces.isEmpty()) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("totalCount", 0);
            return Response.ok(new GenericDataRepresentation<>(new ArrayList<>(), Collections.emptyMap(), meta)).build();
        }

        // ES
        Query filterTextQuery;
        if (query.getFilterText() != null && !query.getFilterText().trim().isEmpty() && !query.getFilterText().trim().equals("*")) {
            filterTextQuery = new MultiMatchQuery(query.getFilterText(),
                    IndexedDocumentModel.ASSIGNED_ID,
                    IndexedDocumentModel.SUPPLIER_NAME,
                    IndexedDocumentModel.CUSTOMER_NAME,
                    IndexedDocumentModel.SUPPLIER_ASSIGNED_ID,
                    IndexedDocumentModel.CUSTOMER_ASSIGNED_ID
            );
        } else {
            filterTextQuery = new MatchAllQuery();
        }

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
                    roleQuery = new TermsQuery(IndexedDocumentModel.SUPPLIER_ASSIGNED_ID, allSpaces);
                    break;
                case RECEIVER:
                    roleQuery = new TermsQuery(IndexedDocumentModel.CUSTOMER_ASSIGNED_ID, allSpaces);
                    break;
                default:
                    throw new IllegalStateException("Invalid role:" + query.getRole());
            }
        }

        TermsQuery supplierQuery = new TermsQuery(IndexedDocumentModel.SUPPLIER_ASSIGNED_ID, allSpaces);
        TermsQuery customerQuery = new TermsQuery(IndexedDocumentModel.CUSTOMER_ASSIGNED_ID, allSpaces);


        BoolQuery boolQueryBuilder = new BoolQuery()
                .must(filterTextQuery)
                .should(supplierQuery)
                .should(customerQuery)
                .minimumShouldMatch(1);
        if (typeQuery != null) {
            boolQueryBuilder.filter(typeQuery);
        }
        if (currencyQuery != null) {
            boolQueryBuilder.filter(currencyQuery);
        }
        if (tagSQuery != null) {
            boolQueryBuilder.filter(tagSQuery);
        }
        if (starQuery != null) {
            boolQueryBuilder.filter(starQuery);
        }
        if (amountQuery != null) {
            boolQueryBuilder.filter(amountQuery);
        }
        if (issueDateQuery != null) {
            boolQueryBuilder.filter(issueDateQuery);
        }
        if (roleQuery != null) {
            boolQueryBuilder.must(roleQuery);
        }

        String orderBy;
        if (query.getOrderBy() == null) {
            orderBy = IndexedDocumentModel.ISSUE_DATE;
        } else {
            orderBy = Stream.of(query.getOrderBy().split("(?<=[a-z])(?=[A-Z])")).collect(Collectors.joining("_")).toLowerCase();
        }

        IndexedDocumentQueryModel documentQuery = IndexedDocumentQueryModel.builder()
                .orderBy(orderBy, query.isAsc())
                .offset(query.getOffset() != null ? query.getOffset() : 0)
                .limit(query.getLimit() != null ? query.getLimit() : 10)
                .build();
        SearchResultModel<IndexedDocumentModel> result = indexedDocumentProvider.getDocumentsUser(user, documentQuery);

        // Meta
        Map<String, Object> meta = new HashMap<>();
        meta.put("totalCount", result.getTotalResults());

        // Links
        Map<String, String> links = new HashMap<>();

        return Response.ok(new GenericDataRepresentation<>(result.getItems().stream()
                .map(indexedDocument -> modelToRepresentation.toRepresentation(user, indexedDocument.getDocument(), uriInfo))
                .collect(Collectors.toList()), links, meta)).build();
    }

}
