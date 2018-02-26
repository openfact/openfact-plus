package org.clarksnut.services.resources;

import org.clarksnut.models.*;
import org.clarksnut.representations.idm.GenericDataRepresentation;
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
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@Path("/parties")
public class PartyService {

    private static final Logger logger = Logger.getLogger(PartyService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private PartyProvider partyProvider;

    @Inject
    private UserProvider userProvider;

    @Inject
    private SpaceProvider spaceProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

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
    public GenericDataRepresentation getParties(
            @Context HttpServletRequest httpServletRequest,
            @QueryParam("q") String query,
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("spaceIds") List<String> spaceIds) throws ErrorResponseException {
        List<PartyModel> parties;
        if (spaceIds != null && !spaceIds.isEmpty()) {
            SpaceModel[] spaces = spaceIds.stream().map(f -> spaceProvider.getByAssignedId(f)).toArray(SpaceModel[]::new);
            parties = partyProvider.getParties(query, limit, spaces);
        } else {
            // User context
            UserModel user = getUser(httpServletRequest);
            Set<SpaceModel> spaces = user.getAllPermitedSpaces();
            parties = partyProvider.getParties(query, limit, spaces.toArray(new SpaceModel[spaces.size()]));
        }

        return new GenericDataRepresentation<>(parties.stream()
                .map(party -> modelToRepresentation.toRepresentation(party))
                .collect(Collectors.toList()));
    }

}
