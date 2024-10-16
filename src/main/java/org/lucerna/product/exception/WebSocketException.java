package org.lucerna.product.exception;


public class WebSocketException extends RuntimeException {
    public WebSocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebSocketException(String message) {
        super(message);
    }
}
