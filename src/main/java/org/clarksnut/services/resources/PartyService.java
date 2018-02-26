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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.*;
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
        UserModel user = getUser(httpServletRequest);

        Map<String, SpaceModel> allPermittedSpaces = new HashMap<>();
        for (SpaceModel space : user.getAllPermitedSpaces()) {
            allPermittedSpaces.put(space.getId(), space);
        }
        if (allPermittedSpaces.isEmpty()) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("totalCount", 0);
            return new GenericDataRepresentation<>(new ArrayList<>(), Collections.emptyMap(), meta);
        }

        Map<String, SpaceModel> selectedSpaces = new HashMap<>();
        if (spaceIds != null && !spaceIds.isEmpty()) {
            for (String spaceId : spaceIds) {
                if (allPermittedSpaces.containsKey(spaceId)) {
                    selectedSpaces.put(spaceId, allPermittedSpaces.get(spaceId));
                }
            }
        } else {
            selectedSpaces = allPermittedSpaces;
        }

        SpaceModel[] spaces = selectedSpaces.values().toArray(new SpaceModel[selectedSpaces.size()]);
        List<PartyModel> parties = partyProvider.getParties(query, limit, spaces);

        return new GenericDataRepresentation<>(parties.stream()
                .map(party -> modelToRepresentation.toRepresentation(party))
                .collect(Collectors.toList()));
    }

}
