package ru.codeunited.flink;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkFixedPartitioner;

import java.util.Optional;
import java.util.Properties;

public class SampleJob {

    public static final String BOOTSTRAP_SERVERS = "kafka1:9092,kafka2:9092,kafka3:9092";

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties propSource = new Properties();
        propSource.setProperty("bootstrap.servers", BOOTSTRAP_SERVERS);
        propSource.setProperty("group.id", "FLINK-SOURCE");

        FlinkKafkaConsumer<String> source = new FlinkKafkaConsumer<>("dev-mpart", new SimpleStringSchema(), propSource);
        source.setStartFromGroupOffsets();

        Properties propSink = new Properties();
        propSink.setProperty("bootstrap.servers", BOOTSTRAP_SERVERS);
        propSink.setProperty("group.id", "FLINK-SINK");
        propSink.setProperty("transaction.timeout.ms", "1000");
        FlinkKafkaProducer<String> sink = new FlinkKafkaProducer<>(
                "sink-topic",
                new SimpleStringSchema(),
                propSink,
                Optional.of(new FlinkFixedPartitioner<>())
        );

        MapFunction<String, String> transform = s -> s + " FLINKED";
        FilterFunction<String> notEmptyOnly = value -> value != null && value.trim().length() > 0;

        env
                .addSource(source)
                .filter(notEmptyOnly)
                .map(transform)
                .addSink(sink).setParallelism(4);

        env.execute();
    }

}
