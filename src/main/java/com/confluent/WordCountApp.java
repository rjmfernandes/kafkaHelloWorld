package com.confluent;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

public class WordCountApp {
    public static final String SOURCE_TOPIC_WORD_COUNT_INPUT = "word-count-input";
    public static final String SINK_TOPIC_WORD_COUNT_OUTPUT = "word-count-output";
    public static final String WORDCOUNT_APPLICATION = "wordcount-app";
    private static final Logger log = LoggerFactory.getLogger(WordCountApp.class);

    private static Properties config() {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, WORDCOUNT_APPLICATION);
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        //not for production
        config.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);
        config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG,1000);
        return config;
    }

    private static Topology createTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        //build stream from topic
        KStream<String, String> textLines = builder.stream(SOURCE_TOPIC_WORD_COUNT_INPUT);
        KTable<String, Long> wordCounts = textLines
                // map values to lower case - stateless
                .mapValues(textLine -> textLine.toLowerCase())
                // flat map values splitting into tokens separated by non-alphanumeric characters
                // meaning spaces for example
                .flatMapValues(textLine -> Arrays.asList(textLine.split("\\W+")))
                // repartition using value as key for the group by key after
                .selectKey((key, word) -> word)
                // group by key before count
                .groupByKey()
                // materialize into a store named Counts
                .count(Materialized.as("Counts"));

        // stream output to a topic
        wordCounts.toStream().to(SINK_TOPIC_WORD_COUNT_OUTPUT,
                Produced.with(Serdes.String(), Serdes.Long()));
        //return the topology
        return builder.build();
    }

    public static void main(String[] args) {
        Properties config = config();
        Topology topology = createTopology();
        log.info("TOPOLOGY:\n" + topology.describe());
        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.cleanUp(); //in production only under controlled circumstances
        streams.start();

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        while (true) {
            log.info("State: " + streams.state().name());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }


}
