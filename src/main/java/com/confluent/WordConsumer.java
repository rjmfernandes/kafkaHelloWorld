package com.confluent;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class WordConsumer {
    private static final Logger log = LoggerFactory.getLogger(WordConsumer.class);
    public static final String INPUT_TOPIC = "word-count-input";
    public static final String CONSUMER_GROUP_ID = "word-consumer";
    public static final int POLL_DURATION_MS = 100;

    private static Properties config() {
        Properties config = new Properties();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        return config;
    }

    /**
     * Reads from topic.
     *
     * @param args
     */
    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(config());
        consumer.subscribe(Arrays.asList(INPUT_TOPIC));
        log.info("Reading topic " + INPUT_TOPIC);
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(POLL_DURATION_MS));
                for (ConsumerRecord<String, String> record : records)
                    log.info("new value:"+ record.value());
            }
        } finally {
            consumer.close();
        }

    }
}
