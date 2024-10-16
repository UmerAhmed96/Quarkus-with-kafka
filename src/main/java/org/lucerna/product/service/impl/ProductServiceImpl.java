package org.lucerna.product.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.lucerna.product.dto.ProductDTO;
import org.lucerna.product.exception.ProductException;
import org.lucerna.product.integration.ProductConsumer;
import org.lucerna.product.integration.ProductProducer;
import org.lucerna.product.service.ProductService;
import org.lucerna.product.util.ProductUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service implementation for Product-related business logic.
 * This class handles saving products and retrieving saved product responses
 * by interacting with Kafka through ProductProducer and ProductConsumer.
 */
@ApplicationScoped
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    // Injected dependencies for Kafka Producer and Consumer
    @Inject
    ProductProducer productProducer;

    @Inject
    ProductConsumer productConsumer;

    /**
     * Saves a product by generating a correlation ID, sending the product details to Kafka, and returning the product.
     *
     * @param productDTO ProductDTO containing the product information to be saved
     * @return ProductDTO with the correlation ID set
     * @throws ProductException if an error occurs during message production
     */
    @Override
    public ProductDTO saveMessage(ProductDTO productDTO) {
        try {
            String correlationId = ProductUtils.generateCorrelationId();
            productDTO.setCorrelationId(correlationId);

            productProducer.sendMessage(correlationId, productDTO.getName());

            return productDTO;
        } catch (Exception e) {
            LOGGER.error("Error occurred while saving message for product: {}", productDTO, e);
            throw new ProductException("Failed to save product message", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }

    /**
     * Retrieves a saved product response by consuming the message from Kafka using the correlation ID.
     *
     * @param correlationId String representing the correlation ID used to track the message
     * @return ProductDTO containing the product details retrieved from Kafka
     * @throws ProductException if an error occurs during message consumption
     */
    @Override
    public ProductDTO getSavedProductResponse(String correlationId) {
        try {
            return productConsumer.receiveMessage(correlationId);
        } catch (Exception e) {
            LOGGER.error("Error occurred while retrieving product message with correlation ID: {}", correlationId, e);
            throw new ProductException("Failed to retrieve product message", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }
}
