package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.RequestAccessScope;
import org.clarksnut.models.RequestAccessToSpaceModel;
import org.clarksnut.models.RequestStatusType;
import org.clarksnut.models.jpa.entity.RequestAccessToSpaceEntity;

import javax.persistence.EntityManager;
import java.util.Date;

public class RequestAccessToSpaceAdapter implements RequestAccessToSpaceModel, JpaModel<RequestAccessToSpaceEntity> {

    private final EntityManager em;
    private final RequestAccessToSpaceEntity request;

    public RequestAccessToSpaceAdapter(EntityManager em, RequestAccessToSpaceEntity request) {
        this.em = em;
        this.request = request;
    }

    public static RequestAccessToSpaceEntity toEntity(RequestAccessToSpaceModel model, EntityManager em) {
        if (model instanceof RequestAccessToSpaceAdapter) {
            return ((RequestAccessToSpaceAdapter) model).getEntity();
        }
        return em.getReference(RequestAccessToSpaceEntity.class, model.getId());
    }


    @Override
    public RequestAccessToSpaceEntity getEntity() {
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
    public RequestAccessScope getScope() {
        return request.getScope();
    }

    @Override
    public RequestStatusType getStatus() {
        return request.getStatus();
    }

    @Override
    public void setStatus(RequestStatusType status) {
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
}
