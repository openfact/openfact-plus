package org.clarksnut.models.db.connections;

import org.clarksnut.models.exceptions.ExceptionConverter;

import javax.ejb.Stateless;
import javax.persistence.PersistenceException;

@Stateless
public class JpaExceptionConverter implements ExceptionConverter {

    @Override
    public Throwable convert(Throwable e) {
        if (!(e instanceof PersistenceException)) return null;
        return PersistenceExceptionConverter.convert(e.getCause() != null ? e.getCause() : e);
    }

}
