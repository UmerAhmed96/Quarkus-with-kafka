package org.lucerna.product.integration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.lucerna.product.exception.ProductException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class ProductProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductProducer.class);

    @Inject
    Producer<String, String> producer;

    @ConfigProperty(name = "app.kafka.topic")
    String topicName;

    /**
     * Sends a message to Kafka with the specified correlation ID and product name.
     * The method creates a ProducerRecord with the topic, correlation ID, and product name,
     * and then sends it to Kafka. The operation is synchronous, awaiting confirmation of
     * message delivery. If an error occurs during sending, a ProductException is thrown.
     *
     * @param correlationId String representing the correlation ID to track the message
     * @param productName String containing the product name to be sent in the message payload
     * @throws ProductException if an error occurs while publishing the message
     */
    public void sendMessage(String correlationId, String productName) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, correlationId, productName);
            RecordMetadata metadata = producer.send(record).get();
            LOGGER.debug("Message sent to topic {}, partition {}, offset {}", topicName, metadata.partition(), metadata.offset());

        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error publishing message with correlation ID {}", correlationId, e);
            Thread.currentThread().interrupt();
            throw new ProductException("Failed to send product message", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }
}
