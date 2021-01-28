package ru.codeunited.kafka;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartitionInfo;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;

public class DescribeTopic {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties config = new Properties();
        config.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9091,localhost:9092,localhost:9093");
        AdminClient admin = AdminClient.create(config);

        // Describe all topics
        for (TopicListing topicListing : admin.listTopics().listings().get()) {

            DescribeTopicsResult describeTopicsResult = admin.describeTopics(Collections.singleton(topicListing.name()));
            Map<String, TopicDescription> stringTopicDescriptionMap = describeTopicsResult.all().get();

            Collection<TopicDescription> values = stringTopicDescriptionMap.values();
            for(TopicDescription desc : values) {
                System.out.println("Topic: " + desc.name());
                List<TopicPartitionInfo> partitions = desc.partitions();
                for (TopicPartitionInfo partInfo : partitions) {
                    System.out.println("\t" + partInfo);
                }
            }
        }
    }
}
