package org.openfact.models;

public interface SpaceProvider {

    /**
     * @param assignedId
     * @param user       owner of space
     * @return created space
     */
    SpaceModel addSpace(String assignedId, UserModel user);

    /**
     * @param assignedId
     * @return space
     */
    SpaceModel getByAssignedId(String assignedId);

    boolean removeSpace(SpaceModel space);
}
