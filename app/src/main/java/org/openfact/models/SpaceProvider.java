package org.openfact.models;

public interface SpaceProvider {

    SpaceModel addSpace(String assignedId, UserModel owner);

    SpaceModel getByAssignedId(String assignedId);

}
