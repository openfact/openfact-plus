package org.openfact.models.utils;

import org.openfact.models.*;
import org.openfact.representation.idm.*;
import org.openfact.services.resources.UsersService;

import javax.ejb.Stateless;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

@Stateless
public class ModelToRepresentation {

    public UserRepresentation toRepresentation(UserModel model, UriInfo uriInfo) {
        UserRepresentation rep = new UserRepresentation();

        // General
        rep.setId(model.getIdentityID());
        rep.setType(ModelType.IDENTITIES.getAlias());

        // Links
        GenericLinksRepresentation links = new GenericLinksRepresentation();
        URI self = uriInfo.getBaseUriBuilder()
                .path(UsersService.class)
                .path(UsersService.class, "getUser")
                .build(model.getIdentityID());
        links.setSelf(self.toString());

        rep.setLinks(links);

        // Attributes
        UserDataAttributesRepresentation attributes = new UserDataAttributesRepresentation();
        attributes.setUserID(model.getId());
        attributes.setIdentityID(model.getIdentityID());
        attributes.setProviderType(model.getProviderType());
        attributes.setUsername(model.getUsername());
        attributes.setFullName(model.getFullName());
        attributes.setRegistrationCompleted(model.isRegistrationCompleted());
        attributes.setBio(model.getBio());
        attributes.setEmail(model.getEmail());
        attributes.setCompany(model.getCompany());
        attributes.setImageURL(model.getImageURL());
        attributes.setUrl(model.getUrl());
        attributes.setCreatedAt(model.getCreatedAt());
        attributes.setUpdatedAt(model.getUpdatedAt());

        // Spaces
//        Stream<SpaceRepresentation> sharedSpaces = model.getSharedSpaces().stream()
//                .map(this::toRepresentation);
//        Stream<SpaceRepresentation> ownedSpaces = model.getOwnedSpaces().stream()
//                .map(f -> toRepresentation(f, true));
//
//        attributes.setSpaces(Stream.concat(ownedSpaces, sharedSpaces).collect(Collectors.toList()));
//        attributes.setSpaceRequests(model.getSpaceRequests().stream().map(this::toRepresentation).collect(Collectors.toList()));

        rep.setAttributes(attributes);
        return rep;
    }

    public SpaceRepresentation toRepresentation(SpaceModel model, boolean isOwner) {
        SpaceRepresentation rep = new SpaceRepresentation();

        rep.setId(model.getId());
        rep.setAssignedId(model.getAssignedId());
        rep.setAlias(model.getAlias());

        if (isOwner) {
            rep.setPermissions(Arrays.asList(PermissionType.OWNER.getAlias()));
        }

        return rep;
    }

    public SpaceRepresentation toRepresentation(SharedSpaceModel model) {
        SpaceRepresentation rep = toRepresentation(model.getSpace(), false);
        rep.setPermissions(model.getPermissions().stream().map(PermissionType::getAlias).collect(Collectors.toList()));
        return rep;
    }

    public RequestAccessToSpaceRepresentation toRepresentation(RequestAccessToSpaceModel model) {
        RequestAccessToSpaceRepresentation rep = new RequestAccessToSpaceRepresentation();

        rep.setMessage(model.getMessage());
        rep.setPermissions(model.getPermissions().stream().map(PermissionType::getAlias).collect(Collectors.toList()));
        rep.setStatus(model.getStatus().getAlias());

        return rep;
    }

    public RepositoryRepresentation toRepresentation(UserRepositoryModel model) {
        RepositoryRepresentation rep = new RepositoryRepresentation();

        rep.setId(model.getId());
        rep.setType(model.getType().getAlias());
        rep.setEmail(model.getEmail());
        rep.setLasTimeSynchronized(model.getLastTimeSynchronized());

        return rep;
    }

}
