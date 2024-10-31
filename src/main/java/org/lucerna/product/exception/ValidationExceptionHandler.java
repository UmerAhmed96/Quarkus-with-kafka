package org.lucerna.product.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.validation.ConstraintViolationException;

@Provider
public class ValidationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        StringBuilder message = new StringBuilder("Validation failed: ");
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            message.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("; ");
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(message.toString()).build();
    }
}
