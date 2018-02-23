package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.*;
import org.clarksnut.models.jpa.entity.RequestEntity;

import javax.persistence.EntityManager;
import java.util.Date;

public class RequestAdapter implements RequestModel, JpaModel<RequestEntity> {

    private final EntityManager em;
    private final RequestEntity request;

    public RequestAdapter(EntityManager em, RequestEntity request) {
        this.em = em;
        this.request = request;
    }

    public static RequestEntity toEntity(RequestModel model, EntityManager em) {
        if (model instanceof RequestAdapter) {
            return ((RequestAdapter) model).getEntity();
        }
        return em.getReference(RequestEntity.class, model.getId());
    }


    @Override
    public RequestEntity getEntity() {
        return request;
    }

    @Override
    public String getId() {
        return request.getId();
    }

    @Override
    public String getMessage() {
        return request.getMessage();
    }

    @Override
    public PermissionType getPermission() {
        return request.getPermission();
    }

    @Override
    public RequestStatus getStatus() {
        return request.getStatus();
    }

    @Override
    public void setStatus(RequestStatus status) {
        request.setStatus(status);
    }

    @Override
    public Date getCreatedAt() {
        return request.getCreatedAt();
    }

    @Override
    public Date getUpdatedAt() {
        return request.getUpdatedAt();
    }

    @Override
    public SpaceModel getSpace() {
        return new SpaceAdapter(em, request.getSpace());
    }

    @Override
    public UserModel getUser() {
        return new UserAdapter(em, request.getUser());
    }
}
