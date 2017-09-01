package org.openfact.models.db.jpa.entity;

import java.time.LocalDateTime;

public interface UpdatableEntity {
    void setUpdatedAt(final LocalDateTime dateTime);
}
