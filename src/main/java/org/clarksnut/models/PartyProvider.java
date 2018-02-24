package org.clarksnut.models;

import java.util.List;

public interface PartyProvider {

    PartyModel getPartyByAssignedId(String assignedId);

    List<PartyModel> getParties(String filterText, int limit, SpaceModel... space);

}
