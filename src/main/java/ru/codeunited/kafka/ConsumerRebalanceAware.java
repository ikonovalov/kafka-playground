package ru.codeunited.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

public class ConsumerRebalanceAware {

    private static final Logger log = LoggerFactory.getLogger(ConsumerRebalanceAware.class);

    public static void main(String[] args) {
        Properties properties = new Properties();

        properties.put("bootstrap.servers", "localhost:9091,localhost:9092,localhost:9093");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", System.getProperty("group.id", "GR-ONE"));
        properties.put("client.id", System.getProperty("client.id", "CL-ONE"));
        properties.put("enable.auto.commit", "false");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        CommitOnRebalanceListener rebalanceListener = new CommitOnRebalanceListener(consumer);

        String topic = System.getProperty("topic", "dev-mpart");
        List<String> topics = Collections.singletonList(topic);
        consumer.subscribe(topics, rebalanceListener);
        log.info("Subscribe to {}", topics);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            for (ConsumerRecord<String, String> record : records) {
                log.info("partition={} offset={} key={} value={}",
                        record.partition(),
                        record.offset(),
                        record.key(),
                        record.value()
                );
                rebalanceListener.addOffset(record.topic(), record.partition(), record.offset());
            }
            consumer.commitSync();
        }
    }

    static class CommitOnRebalanceListener implements ConsumerRebalanceListener {

        private final KafkaConsumer<String, String> consumer;

        private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

        public CommitOnRebalanceListener(KafkaConsumer<String, String> consumer) {
            this.consumer = consumer;
        }

        public void addOffset(String topic, int partition, long offset) {
            currentOffsets.put(new TopicPartition(topic, partition), new OffsetAndMetadata(offset, "Commit"));
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            log.info("Partitions Assigned ....");
            for (TopicPartition partition : partitions)
                log.info("{}",partition);
        }

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            log.warn("Following Partitions Revoked ....");
            for (TopicPartition partition : partitions)
                log.warn(partition.partition() + ",");

            consumer.commitSync(currentOffsets);
            log.warn("Following Partitions commited ....");
            for (TopicPartition tp : currentOffsets.keySet())
                log.warn("{}", tp);

            currentOffsets.clear();
        }
    }
}


