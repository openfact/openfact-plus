package org.openfact.models.db.jpa.entity;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.openfact.models.broker.BrokerType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "USER_REPOSITORY")
public class UserRepositoryEntity {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "ID", length = 36)
    private String id;

    @NotNull
    @NotEmpty
    @Column(name = "NAME")
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private BrokerType type;

    @NotNull
    @NotEmpty
    @Email
    @Column(name = "EMAIL")
    private String email;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey)
    private UserEntity user;

    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @Column(name = "LAST_TIME_SYNCHRONIZED")
    private LocalDateTime lastTimeSynchronized;

    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "USER_REPOSITORY_CONFIG", joinColumns = {@JoinColumn(name = "USER_REPOSITORY_ID")})
    private Map<String, String> config;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LocalDateTime getLastTimeSynchronized() {
        return lastTimeSynchronized;
    }

    public void setLastTimeSynchronized(LocalDateTime lastTimeSynchronized) {
        this.lastTimeSynchronized = lastTimeSynchronized;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BrokerType getType() {
        return type;
    }

    public void setType(BrokerType type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
