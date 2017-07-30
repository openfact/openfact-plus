package org.openfact.representation.idm;

import java.util.List;

public class RequestAccessToSpaceRepresentation {

    private String status;
    private String spaceId;
    private String spaceAssignedId;
    private String message;
    private List<String> permissions;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getSpaceAssignedId() {
        return spaceAssignedId;
    }

    public void setSpaceAssignedId(String spaceAssignedId) {
        this.spaceAssignedId = spaceAssignedId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
