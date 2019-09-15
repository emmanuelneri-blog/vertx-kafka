package br.com.emmanuelneri.producer.verticle;

import br.com.emmanuelneri.producer.EventbusAddress;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrderProducerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderProducerVerticle.class);
    private static final String ORDER_TOPIC = "Order";

    @Override
    public void start(final Promise<Void> startPromise) {
        final Map<String, String> config = createKafkaProducerConfig();
        final KafkaProducer<String, String> kafkaProducer = KafkaProducer.create(vertx, config);

        vertx.eventBus().localConsumer(EventbusAddress.ORDER_RECEVIED.name(), message -> {
            final String key = UUID.randomUUID().toString();
            final KafkaProducerRecord<String, String> kafkaProducerRecord = KafkaProducerRecord
                    .create(ORDER_TOPIC, key, message.body().toString());

            kafkaProducer.send(kafkaProducerRecord, result -> {
                if (result.failed()) {
                    LOGGER.error("message produce error {0}", kafkaProducerRecord, result.cause());
                    return;
                }

                LOGGER.info("message produced. key: {0}", kafkaProducerRecord.key());
            });
        });

        startPromise.complete();
    }

    private Map<String, String> createKafkaProducerConfig() {
        final Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return config;
    }
}
