package ru.codeunited.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

public class Producer {

    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
        try {
            long rndPoint = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>("dev-topic", "Message - " + (rndPoint++));
                Future<RecordMetadata> send = kafkaProducer.send(record);
                RecordMetadata recordMetadata = send.get(1, TimeUnit.SECONDS);
                log.info(rndPoint + " => "
                        + recordMetadata.topic()  + ":"
                        + recordMetadata.partition() + ":"
                        + recordMetadata.offset()
                );
                Thread.sleep(500);
            }
        } catch (Exception e) {
            log.error("ru.codeunited.kafka.Producer error", e);
        } finally {
            kafkaProducer.close();
        }
    }
}
