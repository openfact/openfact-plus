package org.openfact.models.utils;

import org.openfact.models.UserModel;
import org.openfact.representation.idm.UserDataAttributesRepresentation;
import org.openfact.representation.idm.UserRepresentation;

import javax.ejb.Stateless;

@Stateless
public class ModelToRepresentation {

    public UserRepresentation toRepresentation(UserModel model) {
        UserRepresentation representation = new UserRepresentation();
        representation.setId(model.getId());

        UserDataAttributesRepresentation attributes = new UserDataAttributesRepresentation();
        attributes.setUsername(model.getUsername());
        attributes.setRegistrationCompleted(model.isRegistrationCompleted());
        attributes.setFullName(model.getFullName());

        representation.setAttributes(attributes);

        return representation;
    }
}
