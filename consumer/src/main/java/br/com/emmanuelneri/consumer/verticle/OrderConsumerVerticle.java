package br.com.emmanuelneri.consumer.verticle;

import br.com.emmanuelneri.consumer.model.Order;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.Map;

public class OrderConsumerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumerVerticle.class);
    private static final String ORDER_TOPIC = "Order";

    @Override
    public void start(final Promise<Void> startPromise) {
        final Map<String, String> config = createKafkaConsumerConfig();
        final KafkaConsumer<String, String> kafkaConsumer = KafkaConsumer.create(vertx, config);

        kafkaConsumer.subscribe(ORDER_TOPIC, result -> {
            if (result.failed()) {
                LOGGER.error("failed to subscribe {0} topic", ORDER_TOPIC, result.cause());
                return;
            }

            LOGGER.info("{0} topic subscribed", ORDER_TOPIC);
        });

        kafkaConsumer.handler(result -> {
            LOGGER.info("message consumed {0}", result);

            final ConsumerRecord<String, String> record = result.record();
            LOGGER.info("key: " + record.key());
            LOGGER.info("Headers: " + record.headers());
            LOGGER.info("Order: " + Json.decodeValue(record.value(), Order.class));
        });
    }

    public Map<String, String> createKafkaConsumerConfig() {
        final Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("group.id", "order-consumer-group");
        config.put("auto.offset.reset", "latest");
        return config;
    }

}