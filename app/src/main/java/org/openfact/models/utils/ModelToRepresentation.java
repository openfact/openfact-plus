package org.openfact.models.utils;

import org.openfact.models.UserModel;
import org.openfact.representation.idm.UserDataAttributes;
import org.openfact.representation.idm.UserRepresentation;

import javax.ejb.Stateless;

@Stateless
public class ModelToRepresentation {

    public UserRepresentation toRepresentation(UserModel model) {
        UserRepresentation representation = new UserRepresentation();

        UserDataAttributes attributes = new UserDataAttributes();
        attributes.setUsername(model.getUsername());

        representation.setAttributes(attributes);

        return representation;
    }
}
