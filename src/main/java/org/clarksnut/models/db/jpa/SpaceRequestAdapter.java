package org.clarksnut.models.db.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.*;
import org.clarksnut.models.db.jpa.entity.SpaceRequestEntity;

import javax.persistence.EntityManager;
import java.util.Date;

public class SpaceRequestAdapter implements SpaceRequestModel, JpaModel<SpaceRequestEntity> {

    private final EntityManager em;
    private final SpaceRequestEntity request;

    public SpaceRequestAdapter(EntityManager em, SpaceRequestEntity request) {
        this.em = em;
        this.request = request;
    }

    public static SpaceRequestEntity toEntity(SpaceRequestModel model, EntityManager em) {
        if (model instanceof SpaceRequestAdapter) {
            return ((SpaceRequestAdapter) model).getEntity();
        }
        return em.getReference(SpaceRequestEntity.class, model.getId());
    }


    @Override
    public SpaceRequestEntity getEntity() {
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
    public RequestType getType() {
        return request.getType();
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
    public String getFileId() {
        return request.getFileId();
    }

    @Override
    public String getFileProvider() {
        return request.getFileProvider();
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
    public UserModel getUser() {
        return new UserAdapter(em, request.getUser());
    }

    @Override
    public SpaceModel getSpace() {
        return new SpaceAdapter(em, request.getSpace());
    }
}
