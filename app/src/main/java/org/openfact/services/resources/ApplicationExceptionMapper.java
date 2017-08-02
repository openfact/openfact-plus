package org.openfact.services.resources;

import org.openfact.services.ErrorResponse;
import org.openfact.services.ErrorResponseException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationExceptionMapper implements ExceptionMapper<ErrorResponseException> {

    @Override
    public Response toResponse(ErrorResponseException e) {
        return ErrorResponse.error(e.getMessage(), e.getStatus());
    }

}
