package org.openfact.models.utils;

import org.openfact.models.Constants;
import org.openfact.models.RequestStatus;
import org.openfact.models.SpaceModel;
import org.openfact.models.UserModel;
import org.openfact.representation.idm.SpaceRepresentation;
import org.openfact.representation.idm.UserDataAttributesRepresentation;
import org.openfact.representation.idm.UserRepresentation;

import javax.ejb.Stateless;
import java.util.List;
import java.util.function.BiFunction;
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

        BiFunction<SpaceModel, String, SpaceRepresentation> toSpaceRepresentation = (space, type) -> {
            SpaceRepresentation rep = new SpaceRepresentation();
            rep.setId(space.getId());
            rep.setAlias(space.getAlias());
            rep.setType(type);
            return rep;
        };

        // Spaces
        List<SpaceRepresentation> spacesRepresentation = Stream.of(
                model.getOwnedSpaces().stream().map(f -> toSpaceRepresentation.apply(f, Constants.USER_SPACE_TYPE_OWNER)),
                model.getMemberSpaces(RequestStatus.ACCEPTED).stream().map(f -> toSpaceRepresentation.apply(f, Constants.USER_SPACE_TYPE_MEMBER_ACCEPTED)),
                model.getMemberSpaces(RequestStatus.REQUESTED).stream().map(f -> toSpaceRepresentation.apply(f, Constants.USER_SPACE_TYPE_MEMBER_REQUESTED)),
                model.getMemberSpaces(RequestStatus.REJECTED).stream().map(f -> toSpaceRepresentation.apply(f, Constants.USER_SPACE_TYPE_MEMBER_REJECTED))
        ).flatMap(f -> f).collect(Collectors.toList());
        attributes.setSpaces(spacesRepresentation);

        representation.setAttributes(attributes);
        return representation;
    }

}
