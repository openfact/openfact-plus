package org.clarksnut.models.db.jpa.entity;

import javax.persistence.*;

@Table(name = "cl_migration_model")
@Entity
public class MigrationModelEntity {

    public static final String SINGLETON_ID = "SINGLETON";

    @Id
    @Column(name = "id", length = 36)
    @Access(AccessType.PROPERTY)
    // we do this because relationships often fetch id, but not entity.  This avoids an extra SQL
    private String id;

    @Column(name = "version", length = 36)
    protected String version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof MigrationModelEntity)) return false;

        MigrationModelEntity that = (MigrationModelEntity) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
