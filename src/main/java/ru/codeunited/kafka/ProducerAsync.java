package ru.codeunited.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;

public class ProducerAsync {

    private static final Logger log = LoggerFactory.getLogger(ProducerAsync.class);

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9091,localhost:9092,localhost:9093");
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        properties.put(ACKS_CONFIG, "1");

        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties)) {
            final AtomicInteger rndPoint = new AtomicInteger(0);
            for (int i = 0; i < 1000; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>("dev-mpart", "msg=" + rndPoint.getAndIncrement());
                kafkaProducer.send(record, (meta, exception) -> {
                    if (exception == null)
                        log.info("{} => {}:p{}:o{}", rndPoint, meta.topic(), meta.partition(), meta.offset());
                    else
                        log.error(exception.getMessage(), exception);
                });

                Thread.sleep(1000);
            }
        } catch (Exception e) {
            log.error("ru.codeunited.kafka.Producer error", e);
        }
    }
}
