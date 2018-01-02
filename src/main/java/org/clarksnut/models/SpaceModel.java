package org.clarksnut.models;

import java.util.Date;
import java.util.List;

public interface SpaceModel {

    String NAME = "name";
    String ASSIGNED_ID = "assignedId";

    String getId();

    String getAssignedId();

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    UserModel getOwner();

    void setOwner(UserModel user);

    Date getCreatedAt();

    Date getUpdatedAt();

    List<UserModel> getCollaborators();

    List<UserModel> getCollaborators(int offset, int limit);

    void addCollaborators(UserModel user);

    boolean removeCollaborators(UserModel user);
}
