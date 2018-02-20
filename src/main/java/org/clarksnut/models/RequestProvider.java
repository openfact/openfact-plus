package org.clarksnut.models;

import java.util.List;

public interface RequestProvider {

    RequestModel addRequest(SpaceModel space, UserModel user, RequestScope scope, String message);

    RequestModel getRequest(String id);

    List<RequestModel> getRequests(UserModel user, RequestStatus status);

    List<RequestModel> getRequests(SpaceModel space, RequestStatus status);
}
