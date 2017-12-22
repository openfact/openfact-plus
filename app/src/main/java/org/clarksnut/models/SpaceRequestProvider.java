package org.clarksnut.models;

import org.clarksnut.files.FileModel;

import java.util.List;

public interface SpaceRequestProvider {

    SpaceRequestModel addRequest(SpaceModel space,
                                 UserModel user,
                                 FileModel userPhotograph,
                                 String fileProvider,
                                 String message,
                                 RequestType type);

    SpaceRequestModel getRequest(String id);

    List<SpaceRequestModel> getRequests(SpaceModel space);

    List<SpaceRequestModel> getRequests(SpaceModel space, int offset, int limit);

}
