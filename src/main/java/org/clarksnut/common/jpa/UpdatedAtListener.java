package org.clarksnut.common.jpa;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Calendar;

public class UpdatedAtListener {

    @PrePersist
    @PreUpdate
    public void setUpdatedAt(final UpdatableEntity entity) {
        entity.setUpdatedAt(Calendar.getInstance().getTime());
    }

}
