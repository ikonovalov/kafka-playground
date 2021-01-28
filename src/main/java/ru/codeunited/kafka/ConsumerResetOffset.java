package ru.codeunited.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConsumerResetOffset {

    public static void main(String[] args) {
        Properties properties = new Properties();

        properties.put("bootstrap.servers", "localhost:9091,localhost:9092,localhost:9093");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", System.getProperty("group.id", "GR-ONE"));
        properties.put("client.id", System.getProperty("client.id", "CL-ONE"));
        properties.put("enable.auto.commit", "false");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        long targetOffset = 0;
        OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(targetOffset);
        TopicPartition topicPartition0 = new TopicPartition("dev-mpart", 0);
        TopicPartition topicPartition1 = new TopicPartition("dev-mpart", 1);

        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        offsets.put(topicPartition0, offsetAndMetadata);
        offsets.put(topicPartition1, offsetAndMetadata);
        consumer.commitSync(offsets);
        System.out.println(offsets);

    }
}
