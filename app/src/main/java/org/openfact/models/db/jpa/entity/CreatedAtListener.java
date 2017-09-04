package org.openfact.models.db.jpa.entity;

import javax.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.Calendar;

public class CreatedAtListener {

    @PrePersist
    public void setCreatedAt(final CreatableEntity entity) {
        entity.setCreatedAt(Calendar.getInstance().getTime());
    }

}
