package org.openfact.models.jpa.entities;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "SYNCRONIZATION")
public class SynchronizationEntity {

    public static final String SINGLETON_ID = "SINGLETON";

    @Id
    @Column(name = "ID", length = 36)
    @Access(AccessType.PROPERTY)
    private String id;

    @Column(name = "HISTORY_ID")
    private BigInteger startHistoryId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigInteger getStartHistoryId() {
        return startHistoryId;
    }

    public void setStartHistoryId(BigInteger historyId) {
        this.startHistoryId = historyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof SynchronizationEntity)) return false;

        SynchronizationEntity that = (SynchronizationEntity) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
