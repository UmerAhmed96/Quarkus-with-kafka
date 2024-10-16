package org.lucerna.product.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.validation.ConstraintViolationException;

@Provider
public class ValidationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        StringBuilder sb = new StringBuilder("Validation failed: ");
        exception.getConstraintViolations().forEach(violation -> sb.append(violation.getMessage()).append("; "));
        return Response.status(Response.Status.BAD_REQUEST).entity(sb.toString()).build();
    }
}
