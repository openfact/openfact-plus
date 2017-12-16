package org.clarksnut.common.jpa;

import java.util.Date;

public interface UpdatableEntity {
    void setUpdatedAt(final Date date);
}
