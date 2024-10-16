package org.lucerna.product.integration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.lucerna.product.dto.ProductDTO;
import org.lucerna.product.exception.ProductException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;

@ApplicationScoped
public class ProductConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductConsumer.class);

    @Inject
    Consumer<String, String> consumer;

    @ConfigProperty(name = "app.consumer.poolTimeout", defaultValue = "10")
    Long poolTimeout;

    @ConfigProperty(name = "app.kafka.topic")
    String topicName;


    /**
     * Receives a message from Kafka based on a specific correlation ID.
     * The method assigns a consumer to a specified topic partition, polls for messages,
     * and retrieves the message that matches the given correlation ID.
     * If the correlation ID matches, the method populates and returns a ProductDTO.
     *
     * @param correlationId String representing the correlation ID to filter the message
     * @return ProductDTO containing the product details retrieved from Kafka
     * @throws ProductException if an error occurs while consuming the message
     */

    public ProductDTO receiveMessage(String correlationId) {
        try {
            consumer.assign(Collections.singletonList(new TopicPartition(topicName, 0)));

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(poolTimeout));
            LOGGER.debug("Polled {} records from topic {}", records.count(), topicName);

            ProductDTO productDTO = new ProductDTO();
            records.forEach(record -> {
                if (correlationId.equals(record.key())) {
                    productDTO.setName(record.value());
                    productDTO.setCorrelationId(correlationId);
                }
            });

            consumer.commitAsync();
            consumer.unsubscribe();
            return productDTO;

        } catch (Exception e) {
            LOGGER.error("Error receiving message with correlation ID {}", correlationId, e);
            throw new ProductException("Failed to receive product message", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }
}
