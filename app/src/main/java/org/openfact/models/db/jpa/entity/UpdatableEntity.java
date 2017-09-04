package org.openfact.models.db.jpa.entity;

import java.util.Date;

public interface UpdatableEntity {
    void setUpdatedAt(final Date date);
}
