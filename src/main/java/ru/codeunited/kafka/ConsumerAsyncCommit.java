package ru.codeunited.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class ConsumerAsyncCommit {

    private static final Logger log = LoggerFactory.getLogger(ConsumerAsyncCommit.class);

    public static void main(String[] args) {
        Properties properties = new Properties();

        properties.put("bootstrap.servers", "localhost:9091,localhost:9092,localhost:9093");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", System.getProperty("group.id", "GR-ONE"));
        properties.put("client.id", System.getProperty("client.id", "CL-ONE"));
        properties.put("enable.auto.commit", "false");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(Collections.singletonList("dev-mpart"));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            for (ConsumerRecord<String, String> record : records) {
                log.info("partition={} offset={} key={} value={}",
                        record.partition(),
                        record.offset(),
                        record.key(),
                        record.value()
                );
            }
            consumer.commitAsync((offsets, exception) -> {
                Optional.ofNullable(exception).ifPresent(e -> log.error("Transaction failed"));
            });
        }
    }
}
