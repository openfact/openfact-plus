package org.openfact.models.jpa.entity;

import org.openfact.models.RequestStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "USER_SPACE")
public class UserSpaceEntity {

    private UserEntity user;
    private SpaceEntity space;

    @Column(name = "STATUS")
    private RequestStatus status;

}
