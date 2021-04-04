package ru.codeunited.kafka;

import org.apache.kafka.clients.admin.AdminClient;

import java.util.Collections;
import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;

public class DeleteTopic {
    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9091,localhost:9092,localhost:9093");
        AdminClient admin = AdminClient.create(config);

        try {
            String topic = System.getProperty("topic", "dev-mpart");
            admin.deleteTopics(Collections.singleton(topic)).all().get();
            System.out.println("Deleted");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
