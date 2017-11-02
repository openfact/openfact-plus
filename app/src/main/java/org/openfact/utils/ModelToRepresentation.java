package org.openfact.utils;

import org.openfact.documents.DocumentModel;
import org.openfact.models.ModelType;
import org.openfact.models.SpaceModel;
import org.openfact.models.UserModel;
import org.openfact.representations.idm.*;
import org.openfact.services.resources.DocumentsService;
import org.openfact.services.resources.FilesService;
import org.openfact.services.resources.SpacesService;
import org.openfact.services.resources.UsersService;

import javax.ejb.Stateless;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashSet;
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

    public DocumentRepresentation.Data toRepresentation(DocumentModel model, UriInfo uriInfo) {
        DocumentRepresentation.Data rep = new DocumentRepresentation.Data();

        rep.setId(model.getId());
        rep.setType(ModelType.UBL_DOCUMENT.getAlias());

        // Links
        DocumentRepresentation.DocumentLink links = new DocumentRepresentation.DocumentLink();
        URI self = uriInfo.getBaseUriBuilder()
                .path(DocumentsService.class)
                .path(DocumentsService.class, "getDocument")
                .build(model.getId());
        URI fileLink = uriInfo.getBaseUriBuilder()
                .path(FilesService.class)
                .path(FilesService.class, "getFile")
                .build(model.getFileId());

        links.setSelf(self.toString());
        links.setFilelink(fileLink.toString());

        rep.setLinks(links);

        // Relationships
        DocumentRepresentation.Relationships relationships = new DocumentRepresentation.Relationships();
        DocumentRepresentation.OwnedBy ownedBy = new DocumentRepresentation.OwnedBy();
        relationships.setOwnedBy(ownedBy);
        rep.setRelationships(relationships);

//        List<SpaceRepresentation.Data> owners = model.getSpaces().stream().map(space -> {
//            SpaceRepresentation.Data spaceData = new SpaceRepresentation.Data();
//            spaceData.setId(space.getId());
//            spaceData.setType(ModelType.SPACES.getAlias());
//            return spaceData;
//        }).collect(Collectors.toList());
//        ownedBy.setData(owners); // save

//        GenericLinksRepresentation ownedLinks = new GenericLinksRepresentation();
//        ownedLinks.setSelf(uriInfo.getBaseUriBuilder()
//                .path(SpacesService.class)
//                .path(SpacesService.class, "getSpace")
//                .build(model.getSpace().getId()).toString());
//        ownedBy.setLinks(ownedLinks); // save

        // Attributes
        DocumentRepresentation.Attributes attributes = new DocumentRepresentation.Attributes();
        rep.setAttributes(attributes);

        attributes.setId(model.getId());
        attributes.setAssignedId(model.getAssignedId());
        attributes.setType(model.getType());
        attributes.setAmount(model.getAmount());
        attributes.setCurrency(model.getCurrency());
        attributes.setIssueDate(model.getIssueDate());
        attributes.setSupplierName(model.getSupplierName());
        attributes.setSupplierAssignedId(model.getSupplierAssignedId());
        attributes.setCustomerName(model.getCustomerName());
        attributes.setCustomerAssignedId(model.getCustomerAssignedId());
        attributes.setProvider(model.getProvider().toString());
        attributes.setStarred(model.isStarred());
        attributes.setTags(new HashSet<>(model.getTags()));
        attributes.setCreatedAt(model.getCreatedAt());
        attributes.setUpdatedAt(model.getUpdatedAt());

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
//        rep.setType(model.getJaxb().getName());
//        rep.setEmail(model.getEmail());
//        rep.setLasTimeSynchronized(model.getLastTimeSynchronized());
//
//        return rep;
//    }

}
