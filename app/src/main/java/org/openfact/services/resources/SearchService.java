package org.openfact.services.resources;

import org.openfact.models.QueryModel;
import org.openfact.models.SpaceProvider;
import org.openfact.models.UserProvider;
import org.openfact.representations.idm.GenericDataRepresentation;
import org.openfact.utils.ModelToRepresentation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.stream.Collectors;

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
    private ModelToRepresentation modelToRepresentation;

    @GET
    @Path("spaces")
    public GenericDataRepresentation searchSpaces(@QueryParam("q") String filterText) {
        QueryModel.Builder queryBuilder = QueryModel.builder();

        if (filterText != null) {
            queryBuilder.filterText(filterText);
        }

        return new GenericDataRepresentation(spaceProvider.getSpaces(queryBuilder.build()).stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList()));
    }

    @GET
    @Path("users")
    public GenericDataRepresentation searchUsers(@QueryParam("q") String filterText) {
        QueryModel.Builder queryBuilder = QueryModel.builder();

        if (filterText != null) {
            queryBuilder.filterText(filterText);
        }

        return new GenericDataRepresentation(userProvider.getUsers(queryBuilder.build()).stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList()));
    }

}
