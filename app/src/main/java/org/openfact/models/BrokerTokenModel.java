package org.openfact.models;

public class BrokerTokenModel {

    private String email;

    private BrokerType type;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public BrokerType getType() {
        return type;
    }

    public void setType(BrokerType type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
