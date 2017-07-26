package org.openfact.representation.idm;

public class ExtProfileRepresentation {

    private Boolean registrationCompleted;
    private ContextInformationRepresentation contextInformation;

    public Boolean getRegistrationCompleted() {
        return registrationCompleted;
    }

    public void setRegistrationCompleted(Boolean registrationCompleted) {
        this.registrationCompleted = registrationCompleted;
    }

    public ContextInformationRepresentation getContextInformation() {
        return contextInformation;
    }

    public void setContextInformation(ContextInformationRepresentation contextInformation) {
        this.contextInformation = contextInformation;
    }
}
