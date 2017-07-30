package org.openfact.models.utils;

import org.openfact.models.*;
import org.openfact.representation.idm.SharedSpaceRepresentation;
import org.openfact.representation.idm.SpaceRepresentation;
import org.openfact.representation.idm.UserDataAttributesRepresentation;
import org.openfact.representation.idm.UserRepresentation;

import javax.ejb.Stateless;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
public class ModelToRepresentation {

    public UserRepresentation toRepresentation(UserModel model) {
        UserRepresentation representation = new UserRepresentation();
        representation.setId(model.getId());

        UserDataAttributesRepresentation attributes = new UserDataAttributesRepresentation();
        attributes.setUsername(model.getUsername());
        attributes.setFullName(model.getFullName());
        attributes.setRegistrationCompleted(model.isRegistrationCompleted());

        Function<SpaceModel, SpaceRepresentation> toSpaceRepresentation = space -> {
            SpaceRepresentation rep = new SpaceRepresentation();
            rep.setId(space.getId());
            rep.setAlias(space.getAlias());
            return rep;
        };

        Function<SharedSpaceModel, SharedSpaceRepresentation> toSharedSpaceRepresentation = space -> {
            SharedSpaceRepresentation rep = new SharedSpaceRepresentation();
            rep.setId(space.getSpace().getId());
            rep.setAlias(space.getSpace().getAlias());
            rep.setPermissions(space.getPermissions().stream().map(Enum::toString).collect(Collectors.toList()));
            return rep;
        };

        // Spaces
        attributes.setOwnedSpaces(model.getOwnedSpaces().stream()
                .map(toSpaceRepresentation)
                .collect(Collectors.toList()));
        attributes.setSharedSpaces(model.getSharedSpaces().stream()
                .map(toSharedSpaceRepresentation)
                .collect(Collectors.toList()));

        representation.setAttributes(attributes);
        return representation;
    }

}
