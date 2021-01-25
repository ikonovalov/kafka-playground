package ru.codeunited.kafka;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.errors.TopicExistsException;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;

public class Admin {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties config = new Properties();
        config.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        AdminClient admin = AdminClient.create(config);

        try {
            admin.deleteTopics(Collections.singleton("dev-mpart")).all().get();
            System.out.println("Deleted");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        NewTopic topic = new NewTopic("dev-mpart", 4, (short)1);
        Map<String, String> cfg = new HashMap<>();
        cfg.put("delete.retention.ms", "60000");
        topic.configs(cfg);
        try {
            admin.createTopics(Collections.singleton(topic)).all().get();
            System.out.println("Created");
        } catch (ExecutionException | TopicExistsException alreadyExists) {
            System.err.println(alreadyExists.getMessage());
        }

        // Describe all topics
        for (TopicListing topicListing : admin.listTopics().listings().get()) {

            DescribeTopicsResult describeTopicsResult = admin.describeTopics(Collections.singleton(topicListing.name()));
            Map<String, TopicDescription> stringTopicDescriptionMap = describeTopicsResult.all().get();
            Collection<TopicDescription> values = stringTopicDescriptionMap.values();
            for(TopicDescription desc : values) {
                System.out.println(desc);
            }
        }
    }
}
