package org.openfact.models.db;

import org.openfact.models.db.CreatableEntity;

import javax.persistence.PrePersist;
import java.util.Calendar;

public class CreatedAtListener {

    @PrePersist
    public void setCreatedAt(final CreatableEntity entity) {
        entity.setCreatedAt(Calendar.getInstance().getTime());
    }

}
