package org.openfact.models.db.jpa.entity;

import java.time.LocalDateTime;

public interface CreatableEntity {

    void setCreatedAt(final LocalDateTime dateTime);

}
