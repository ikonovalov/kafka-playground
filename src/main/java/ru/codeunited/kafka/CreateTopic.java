package ru.codeunited.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;

public class CreateTopic {

    private static final Logger log = LoggerFactory.getLogger(CreateTopic.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties config = new Properties();
        config.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9091,localhost:9092,localhost:9093");
        AdminClient admin = AdminClient.create(config);

        String topicName = "dev-mpart";
        short replicationFactor = 2;
        int partitions = 4;

        NewTopic topic = new NewTopic(topicName, partitions, replicationFactor);
        Map<String, String> cfg = new HashMap<>();
        cfg.put("delete.retention.ms", "60000");
        cfg.put("min.insync.replicas", "1"); // default 1
        topic.configs(cfg);
        log.info("{}", topic);
        admin.createTopics(Collections.singleton(topic)).all().get();
        log.info("Created");
    }
}
