package org.clarksnut.models.exceptions;

public interface ExceptionConverter {

    /**
     * Return null if the provider doesn't handle this type
     *
     * @param t
     * @return
     */
    Throwable convert(Throwable t);

}
