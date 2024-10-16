package org.lucerna.product.exception;


public class ProductException extends RuntimeException {
    private final int statusCode;

    public ProductException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
