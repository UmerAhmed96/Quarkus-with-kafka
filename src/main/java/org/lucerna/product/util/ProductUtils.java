package org.lucerna.product.util;

import java.util.UUID;

public class ProductUtils {

    private ProductUtils() {
    }

    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
