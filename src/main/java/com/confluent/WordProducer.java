package com.confluent;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Scanner;

public class WordProducer {
    private static final Logger log = LoggerFactory.getLogger(WordProducer.class);
    public static final String INPUT_TOPIC = "word-count-input";

    private static Properties config() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return config;
    }

    /**
     *
     * Writes to topic message received as input.
     *
     * @param args
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Message : ");
        String message = sc.nextLine();
        sc.close();
        KafkaProducer<String, String> producer = new KafkaProducer<>(config());
        try {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(INPUT_TOPIC, message);
            producer.send(producerRecord);
        } finally {
            producer.close();
        }

    }
}
