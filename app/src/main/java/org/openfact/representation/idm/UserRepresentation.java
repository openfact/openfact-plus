package org.openfact.representation.idm;

public class UserRepresentation {
    
    private String id;
    private String type;
    private UserDataAttributesRepresentation attributes;
    private GenericLinksRepresentation links;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserDataAttributesRepresentation getAttributes() {
        return attributes;
    }

    public void setAttributes(UserDataAttributesRepresentation attributes) {
        this.attributes = attributes;
    }

    public GenericLinksRepresentation getLinks() {
        return links;
    }

    public void setLinks(GenericLinksRepresentation links) {
        this.links = links;
    }

}
