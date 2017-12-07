package org.clarksnut.models.db.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_context_information")
public class UserContextInformationEntity implements Serializable {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "value", length = 4096)
    private String value;

    @OneToOne
    @MapsId
    private UserEntity user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String contextInformation) {
        this.value = contextInformation;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
