package org.openfact.representations.idm;

import java.time.LocalDateTime;

public class RepositoryRepresentation {
    private String id;
    private String type;
    private String email;
    private LocalDateTime lasTimeSynchronized;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setLasTimeSynchronized(LocalDateTime lasTimeSynchronized) {
        this.lasTimeSynchronized = lasTimeSynchronized;
    }

    public LocalDateTime getLasTimeSynchronized() {
        return lasTimeSynchronized;
    }
}
