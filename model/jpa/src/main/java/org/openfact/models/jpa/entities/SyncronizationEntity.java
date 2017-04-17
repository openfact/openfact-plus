package org.openfact.models.jpa.entities;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "SYNCRONIZATION")
public class SyncronizationEntity {

    public static final String SINGLETON_ID = "SINGLETON";

    @Id
    @Column(name = "ID", length = 36)
    @Access(AccessType.PROPERTY)
    private String id;

    @Column(name = "HISTORY_ID")
    private BigInteger historyId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigInteger getHistoryId() {
        return historyId;
    }

    public void setHistoryId(BigInteger historyId) {
        this.historyId = historyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof SyncronizationEntity)) return false;

        SyncronizationEntity that = (SyncronizationEntity) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
