package org.lucerna.product.exception;


import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ProductExceptionHandler implements ExceptionMapper<ProductException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductExceptionHandler.class);

    @Override
    public Response toResponse(ProductException exception) {
        LOGGER.error("Product Exception occurred: {}", exception.getMessage());
        return Response.status(exception.getStatusCode())
                .entity(new ErrorResponse(exception.getMessage(), exception.getStatusCode()))
                .build();
    }
}

class ErrorResponse {
    private String message;
    private int status;

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
