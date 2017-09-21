package org.openfact.models.db;

import java.util.Date;

public interface UpdatableEntity {
    void setUpdatedAt(final Date date);
}
