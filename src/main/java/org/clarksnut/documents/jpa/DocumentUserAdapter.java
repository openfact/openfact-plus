package org.clarksnut.documents.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentUserModel;
import org.clarksnut.documents.jpa.entity.DocumentUserEntity;
import org.clarksnut.models.UserModel;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DocumentUserAdapter implements DocumentUserModel, JpaModel<DocumentUserEntity> {

    private final EntityManager em;
    private final UserModel user;
    private final DocumentUserEntity documentUserEntity;

    public DocumentUserAdapter(EntityManager em, UserModel user, DocumentUserEntity documentUserEntity) {
        this.em = em;
        this.user = user;
        this.documentUserEntity = documentUserEntity;
    }

    @Override
    public DocumentUserEntity getEntity() {
        return documentUserEntity;
    }

    @Override
    public UserModel getUser() {
        return user;
    }

    @Override
    public boolean isStarred() {
        return documentUserEntity.isStarred();
    }

    @Override
    public void setStarred(boolean starred) {
        documentUserEntity.setStarred(starred);
    }

    @Override
    public boolean isViewed() {
        return documentUserEntity.isViewed();
    }

    @Override
    public void setViewed(boolean viewed) {
        documentUserEntity.setViewed(viewed);
    }

    @Override
    public boolean isChecked() {
        return documentUserEntity.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        documentUserEntity.setChecked(checked);
    }

    @Override
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.addAll(documentUserEntity.getTags());
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public void setTags(Set<String> tags) {
        documentUserEntity.setTags(tags);
    }

    @Override
    public Date getCreatedAt() {
        return documentUserEntity.getCreatedAt();
    }

    @Override
    public Date getUpdatedAt() {
        return documentUserEntity.getUpdatedAt();
    }

    @Override
    public DocumentModel getDocument() {
        return new DocumentAdapter(em, documentUserEntity.getDocument());
    }

}
