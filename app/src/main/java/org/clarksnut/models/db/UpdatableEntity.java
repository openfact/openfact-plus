package org.clarksnut.models.db;

import java.util.Date;

public interface UpdatableEntity {
    void setUpdatedAt(final Date date);
}
