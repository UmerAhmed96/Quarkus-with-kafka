package org.lucerna.product.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Properties;

/**
 * KafkaConfig class provides the configuration for Kafka Producer and Consumer.
 * It uses CDI to produce singleton instances for KafkaProducer and KafkaConsumer,
 * which can then be injected into other application components.
 */
@ApplicationScoped
public class KafkaConfig {

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String bootstrapServers;

    /**
     * Creates and configures a Kafka Producer instance.
     * The producer is application-scoped and will be shared across the application.
     *
     * @return a configured Kafka Producer instance
     */
    @Produces
    @ApplicationScoped
    public Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        props.put("acks", "1");

        return new KafkaProducer<>(props);
    }

    /**
     * Creates and configures a Kafka Consumer instance.
     * The consumer is application-scoped and will be shared across the application.
     *
     * @return a configured Kafka Consumer instance
     */
    @Produces
    @ApplicationScoped
    public Consumer<String, String> createConsumer() {
        Properties props = new Properties();

        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "quarkus-consumer-group");  // Change this ID as needed for different consumer groups
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("auto.offset.reset", "earliest");  // "earliest" means it will start from the beginning if no offset exists

        return new KafkaConsumer<>(props);
    }
}
