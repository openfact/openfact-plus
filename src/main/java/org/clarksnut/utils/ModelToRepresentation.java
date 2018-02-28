package org.clarksnut.utils;

import org.clarksnut.models.*;
import org.clarksnut.representations.idm.*;
import org.clarksnut.services.resources.DocumentsService;
import org.clarksnut.services.resources.SpacesService;
import org.clarksnut.services.resources.UsersService;

import javax.ejb.Stateless;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ModelToRepresentation {

    public UserRepresentation.UserData toRepresentation(UserModel model, UriInfo uriInfo, boolean fullInfo) {
        UserRepresentation.UserData rep = new UserRepresentation.UserData();

        rep.setId(model.getId());
        rep.setType(ModelType.IDENTITIES.getAlias());

        // Links
        GenericLinksRepresentation links = new GenericLinksRepresentation();
        URI self = uriInfo.getBaseUriBuilder()
                .path(UsersService.class)
                .path(UsersService.class, "getUser")
                .build(model.getId());
        links.setSelf(self.toString());

        rep.setLinks(links);

        // Attributes
        UserRepresentation.UserAttributesRepresentation attributes = new UserRepresentation.UserAttributesRepresentation();
        attributes.setUserID(model.getId());
        attributes.setUsername(model.getUsername());
        attributes.setFullName(model.getFullName());
        attributes.setBio(model.getBio());
        attributes.setEmail(model.getEmail());
        attributes.setCompany(model.getCompany());
        attributes.setImageURL(model.getImageURL());
        attributes.setUrl(model.getUrl());

        if (fullInfo) {
            attributes.setProviderType(model.getProviderType());
            attributes.setDefaultLanguage(model.getDefaultLanguage());
            attributes.setRegistrationCompleted(model.isRegistrationCompleted());

            attributes.setCreatedAt(model.getCreatedAt());
            attributes.setUpdatedAt(model.getUpdatedAt());
        }

        rep.setAttributes(attributes);
        return rep;
    }

    public SpaceRepresentation.SpaceData toRepresentation(SpaceModel model, UriInfo uriInfo, boolean fullInfo) {
        SpaceRepresentation.SpaceData rep = new SpaceRepresentation.SpaceData();

        rep.setId(model.getId());
        rep.setType(ModelType.SPACES.getAlias());

        // Links
        GenericLinksRepresentation links = new GenericLinksRepresentation();
        rep.setLinks(links);

        URI self = uriInfo.getBaseUriBuilder()
                .path(SpacesService.class)
                .path(SpacesService.class, "getSpace")
                .build(model.getId());
        links.setSelf(self.toString());

        // Relationships
        SpaceRepresentation.SpaceRelationships relationships = new SpaceRepresentation.SpaceRelationships();
        rep.setRelationships(relationships);

        // Relationships Owner
        UserModel ownerUser = model.getOwner();

        SpaceRepresentation.SpaceOwnedBy ownedBy = new SpaceRepresentation.SpaceOwnedBy();
        relationships.setOwnedBy(ownedBy);

        UserRepresentation.UserData ownerData = new UserRepresentation.UserData();
        ownedBy.setData(ownerData);

        ownerData.setId(ownerUser.getId());
        ownerData.setType(ModelType.IDENTITIES.getAlias());

        GenericLinksRepresentation ownedLinks = new GenericLinksRepresentation();
        ownedBy.setLinks(ownedLinks);

        ownedLinks.setSelf(uriInfo.getBaseUriBuilder()
                .path(UsersService.class)
                .path(UsersService.class, "getUser")
                .build(ownerUser.getId()).toString());

        // Attributes
        SpaceRepresentation.SpaceAttributes attributes = new SpaceRepresentation.SpaceAttributes();
        rep.setAttributes(attributes);

        attributes.setName(model.getName());
        attributes.setAssignedId(model.getAssignedId());

        if (fullInfo) {
            attributes.setDescription(model.getDescription());
            attributes.setCreatedAt(model.getCreatedAt());
            attributes.setUpdatedAt(model.getUpdatedAt());
        }

        return rep;
    }

    public DocumentRepresentation.DocumentData toRepresentation(UserModel user, DocumentModel model, UriInfo uriInfo) {
        DocumentRepresentation.DocumentData rep = new DocumentRepresentation.DocumentData();

        List<DocumentVersionModel> documentVersions = model.getVersions();


        rep.setId(model.getId());
        rep.setType(ModelType.UBL_DOCUMENT.getAlias());

        // Links
        DocumentRepresentation.DocumentLink links = new DocumentRepresentation.DocumentLink();
        URI self = uriInfo.getBaseUriBuilder()
                .path(DocumentsService.class)
                .path(DocumentsService.class, "getDocument")
                .build(model.getId());

        links.setSelf(self.toString());

        rep.setLinks(links);

        // Attributes
        DocumentRepresentation.DocumentAttributes attributes = new DocumentRepresentation.DocumentAttributes();
        rep.setAttributes(attributes);

        attributes.setId(model.getId());
        attributes.setType(model.getType());
        attributes.setAssignedId(model.getAssignedId());
        attributes.setSupplierAssignedId(model.getSupplierAssignedId());
        attributes.setCustomerAssignedId(model.getCustomerAssignedId());

        attributes.setIssueDate(model.getIssueDate());
        attributes.setCurrency(model.getCurrency());
        attributes.setAmount(model.getAmount());
        attributes.setTax(model.getTax());

        attributes.setSupplierName(model.getSupplierName());
        attributes.setSupplierStreetAddress(model.getSupplierStreetAddress());
        attributes.setSupplierCity(model.getSupplierCity());
        attributes.setSupplierCountry(model.getSupplierCountry());
        attributes.setCustomerName(model.getCustomerName());
        attributes.setCustomerStreetAddress(model.getCustomerStreetAddress());
        attributes.setCustomerCity(model.getCustomerCity());
        attributes.setCustomerCountry(model.getCustomerCountry());

        attributes.setViewed(model.getUserViews().contains(user.getId()));
        attributes.setStarred(model.getUserStars().contains(user.getId()));
        attributes.setChecked(model.getUserChecks().contains(user.getId()));

        attributes.setCreatedAt(model.getCreatedAt());
        attributes.setUpdatedAt(model.getUpdatedAt());

        attributes.setVersions(documentVersions.stream().map(DocumentVersionModel::getId).collect(Collectors.toSet()));

        return rep;
    }

    public RequestRepresentation.RequestData toRepresentation(RequestModel model) {
        RequestRepresentation.RequestData rep = new RequestRepresentation.RequestData();

        rep.setId(model.getId());
        rep.setType(ModelType.REQUEST_ACCESS.getAlias());

        // Attributes
        RequestRepresentation.RequestAttributes attributes = new RequestRepresentation.RequestAttributes();
        rep.setAttributes(attributes);

        attributes.setSpace(model.getSpace().getId());
        attributes.setUser(model.getUser().getId()); // WARNING: REMOVE IDENTITY ID
        attributes.setScope(model.getPermission().toString());
        attributes.setMessage(model.getMessage());
        attributes.setStatus(model.getStatus().toString());
        attributes.setCreatedAt(model.getCreatedAt());
        attributes.setUpdatedAt(model.getUpdatedAt());

        return rep;
    }

    public PartyRepresentation.PartyData toRepresentation(PartyModel party) {
        PartyRepresentation.PartyData rep = new PartyRepresentation.PartyData();
        rep.setId(party.getId());
        rep.setType(ModelType.PARTIES.getAlias());

        PartyRepresentation.PartyAttributes attributes = new PartyRepresentation.PartyAttributes();
        rep.setAttributes(attributes);

        attributes.setName(party.getName());
        attributes.setAssignedId(party.getAssignedId());

        return rep;
    }

    public FacetRepresentation toRepresentation(FacetModel model) {
        FacetRepresentation rep = new FacetRepresentation();

        rep.setValue(model.getValue());
        rep.setCount(model.getCount());
        return rep;
    }


}
