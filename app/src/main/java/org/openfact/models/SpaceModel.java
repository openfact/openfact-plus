package org.openfact.models;

public interface SpaceModel {

    String getId();

    String getAlias();

    UserModel getOwner();

    void setOwner(UserModel user);

    RequestStatus requestMemberApproval(UserModel user);
}
