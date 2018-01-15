package org.clarksnut.utils;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentVersionModel;
import org.clarksnut.documents.IndexedDocumentModel;
import org.clarksnut.models.ModelType;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
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

    public UserRepresentation.Data toRepresentation(UserModel model, UriInfo uriInfo) {
        UserRepresentation.Data rep = new UserRepresentation.Data();

        rep.setId(model.getIdentityID());
        rep.setType(ModelType.IDENTITIES.getAlias());

        // Links
        GenericLinksRepresentation links = new GenericLinksRepresentation();
        URI self = uriInfo.getBaseUriBuilder()
                .path(UsersService.class)
                .path(UsersService.class, "getUser")
                .build(model.getIdentityID());
        links.setSelf(self.toString());

        rep.setLinks(links);

        // Attributes
        UserAttributesRepresentation attributes = new UserAttributesRepresentation();
        attributes.setUserID(model.getId());
        attributes.setIdentityID(model.getIdentityID());
        attributes.setProviderType(model.getProviderType());
        attributes.setUsername(model.getUsername());
        attributes.setFullName(model.getFullName());
        attributes.setRegistrationCompleted(model.isRegistrationCompleted());
        attributes.setBio(model.getBio());
        attributes.setEmail(model.getEmail());
        attributes.setCompany(model.getCompany());
        attributes.setImageURL(model.getImageURL());
        attributes.setUrl(model.getUrl());
        attributes.setCreatedAt(model.getCreatedAt());
        attributes.setUpdatedAt(model.getUpdatedAt());

        attributes.setContextInformation(model.getContextInformation());
        attributes.setFavoriteSpaces(model.getFavoriteSpaces());
        attributes.setOwnedSpaces(model.getOwnedSpaces().stream().map(SpaceModel::getAssignedId).collect(Collectors.toSet()));
        attributes.setCollaboratedSpaces(model.getCollaboratedSpaces().stream().map(SpaceModel::getAssignedId).collect(Collectors.toSet()));

        attributes.setLanguage(model.getLanguage());

        rep.setAttributes(attributes);
        return rep;
    }

    public SpaceRepresentation.Data toRepresentation(SpaceModel model, UriInfo uriInfo) {
        SpaceRepresentation.Data rep = new SpaceRepresentation.Data();

        rep.setId(model.getId());
        rep.setType(ModelType.SPACES.getAlias());

        // Links
        GenericLinksRepresentation links = new GenericLinksRepresentation();
        URI self = uriInfo.getBaseUriBuilder()
                .path(SpacesService.class)
                .path(SpacesService.class, "getSpace")
                .build(model.getId());
        links.setSelf(self.toString());

        rep.setLinks(links);

        // Relationships
        SpaceRepresentation.Relationships relationships = new SpaceRepresentation.Relationships();
        SpaceRepresentation.OwnedBy ownedBy = new SpaceRepresentation.OwnedBy();
        relationships.setOwnedBy(ownedBy);
        rep.setRelationships(relationships);

        if (model.getOwner() != null) {
            UserRepresentation.Data userData = new UserRepresentation.Data();
            userData.setId(model.getOwner().getIdentityID());
            userData.setType(ModelType.IDENTITIES.getAlias());
            ownedBy.setData(userData); // save

            GenericLinksRepresentation ownedLinks = new GenericLinksRepresentation();
            ownedLinks.setSelf(uriInfo.getBaseUriBuilder()
                    .path(UsersService.class)
                    .path(UsersService.class, "getUser")
                    .build(model.getOwner().getIdentityID()).toString());
            ownedBy.setLinks(ownedLinks); // save
        }

        // Attributes
        SpaceRepresentation.Attributes attributes = new SpaceRepresentation.Attributes();
        rep.setAttributes(attributes);

        attributes.setName(model.getName());
        attributes.setAssignedId(model.getAssignedId());
        attributes.setDescription(model.getDescription());
        attributes.setCreatedAt(model.getCreatedAt());
        attributes.setUpdatedAt(model.getUpdatedAt());

        return rep;
    }

    public DocumentRepresentation.Data toRepresentation(UserModel user, DocumentModel model, UriInfo uriInfo) {
        DocumentRepresentation.Data rep = new DocumentRepresentation.Data();

        IndexedDocumentModel indexedDocument = model.getIndexedDocument();
        DocumentVersionModel documentCurrentVersion = model.getCurrentVersion();
        List<DocumentVersionModel> documentVersions = model.getVersions();


        rep.setId(model.getId());
        rep.setType(ModelType.UBL_DOCUMENT.getAlias());

        // Links
        DocumentRepresentation.DocumentLink links = new DocumentRepresentation.DocumentLink();
        URI self = uriInfo.getBaseUriBuilder()
                .path(DocumentsService.class)
                .path(DocumentsService.class, "getIndexedDocument")
                .build(model.getId());

        links.setSelf(self.toString());

        rep.setLinks(links);

        // Attributes
        DocumentRepresentation.Attributes attributes = new DocumentRepresentation.Attributes();
        rep.setAttributes(attributes);

        attributes.setId(model.getId());
        attributes.setType(model.getType());
        attributes.setAssignedId(model.getAssignedId());
        attributes.setSupplierAssignedId(model.getSupplierAssignedId());

        attributes.setIssueDate(documentCurrentVersion.getIssueDate());
        attributes.setCurrency(documentCurrentVersion.getCurrency());
        attributes.setAmount(documentCurrentVersion.getAmount());
        attributes.setTax(documentCurrentVersion.getTax());

        attributes.setSupplierName(documentCurrentVersion.getSupplierName());
        attributes.setSupplierStreetAddress(documentCurrentVersion.getSupplierStreetAddress());
        attributes.setSupplierCity(documentCurrentVersion.getSupplierCity());
        attributes.setSupplierCountry(documentCurrentVersion.getSupplierCountry());
        attributes.setCustomerAssignedId(documentCurrentVersion.getCustomerAssignedId());
        attributes.setCustomerName(documentCurrentVersion.getCustomerName());
        attributes.setCustomerStreetAddress(documentCurrentVersion.getCustomerStreetAddress());
        attributes.setCustomerCity(documentCurrentVersion.getCustomerCity());
        attributes.setCustomerCountry(documentCurrentVersion.getCustomerCountry());

        attributes.setViewed(indexedDocument.getUserViews().contains(user.getId()));
        attributes.setStarred(indexedDocument.getUserStars().contains(user.getId()));
        attributes.setChecked(indexedDocument.getUserChecks().contains(user.getId()));

        attributes.setCreatedAt(model.getCreatedAt());
        attributes.setUpdatedAt(model.getUpdatedAt());

        attributes.setVersions(documentVersions.stream().map(DocumentVersionModel::getId).collect(Collectors.toSet()));

        return rep;
    }

//    public SpaceRepresentation toRepresentation(SharedSpaceModel model) {
//        SpaceRepresentation rep = toRepresentation(model.getSpace(), false);
//        rep.setPermissions(model.getPermissions().stream().getUblMessages(PermissionType::getName).collect(Collectors.toList()));
//        return rep;
//    }
//
//    public RequestAccessToSpaceRepresentation toRepresentation(RequestAccessToSpaceModel model) {
//        RequestAccessToSpaceRepresentation rep = new RequestAccessToSpaceRepresentation();
//
//        rep.setMessage(model.getMessage());
//        rep.setPermissions(model.getPermissions().stream().getUblMessages(PermissionType::getName).collect(Collectors.toList()));
//        rep.setStatus(model.getStatus().getName());
//
//        return rep;
//    }
//
//    public RepositoryRepresentation toRepresentation(UserRepositoryModel model) {
//        RepositoryRepresentation rep = new RepositoryRepresentation();
//
//        rep.setId(model.getId());
//        rep.setType(model.getType().getName());
//        rep.setEmail(model.getEmail());
//        rep.setLasTimeSynchronized(model.getLastTimeSynchronized());
//
//        return rep;
//    }

}
