package org.openfact.models;

import java.util.List;

public interface SpaceProvider {

    /**
     * @param assignedId
     * @param user       owner of space
     * @return created space
     */
    SpaceModel addSpace(String assignedId, String name, UserModel user);

    /**
     * Find space by id
     *
     * @param id
     * @return space
     */
    SpaceModel getSpace(String id);

    /**
     * @param assignedId
     * @return space
     */
    SpaceModel getByAssignedId(String assignedId);

    boolean removeSpace(SpaceModel space);

    List<SpaceModel> getSpaces(UserModel user);

    List<SpaceModel> getSpaces(UserModel user, int offset, int limit);

    List<SpaceModel> getSpaces(QueryModel query);

}
