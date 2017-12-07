package org.clarksnut.models.db;

import javax.persistence.PrePersist;
import java.util.Calendar;

public class CreatedAtListener {

    @PrePersist
    public void setCreatedAt(final CreatableEntity entity) {
        entity.setCreatedAt(Calendar.getInstance().getTime());
    }

}
