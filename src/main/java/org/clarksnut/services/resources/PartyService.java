package org.clarksnut.services.resources;

import org.clarksnut.models.*;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.representations.idm.PartyRepresentation;
import org.clarksnut.services.ErrorResponseException;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

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
@Path("/parties")
public class PartyService extends AbstractResource {

    private static final Logger logger = Logger.getLogger(PartyService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private PartyProvider partyProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GenericDataRepresentation getParties(
            @QueryParam("q") String searchText,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("space") List<String> spaceIds,
            @Context HttpServletRequest httpServletRequest) throws ErrorResponseException {
        UserModel user = getUserSession(httpServletRequest);
        Set<SpaceModel> spaces = filterAllowedSpaces(user, spaceIds);

        List<PartyRepresentation.Data> parties = partyProvider.getParties(searchText, limit, spaces.toArray(new SpaceModel[spaces.size()]))
                .stream()
                .map(party -> modelToRepresentation.toRepresentation(party))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(parties);
    }

}
