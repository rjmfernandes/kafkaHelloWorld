package com.confluent;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class WordCountTest {

    private static TopologyTestDriver testDriver;
    private static TestInputTopic inputTopic;
    private static TestOutputTopic outputTopic;
    private static final Logger log = LoggerFactory.getLogger(WordCountTest.class);
    static final Serde<Bytes> nullSerde = Serdes.Bytes();
    static final Serde<String> stringSerde = Serdes.String();
    static final Serde<Long> longSerde = Serdes.Long();

    @BeforeAll
    public static void setup() {
        Topology topology = WordCountApp.createTopology();
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, stringSerde.getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, stringSerde.getClass());
        testDriver = new TopologyTestDriver(topology, props);
        inputTopic =
                testDriver.createInputTopic(
                        WordCountApp.SOURCE_TOPIC_WORD_COUNT_INPUT, nullSerde.serializer(), stringSerde.serializer());
        outputTopic =
                testDriver.createOutputTopic(
                        WordCountApp.SINK_TOPIC_WORD_COUNT_OUTPUT,
                        stringSerde.deserializer(),
                        longSerde.deserializer());
    }

    @Test
    public void testOneWord() {
        final String nullKey = null;
        inputTopic.pipeInput(nullKey, "Hello", 1L);
        final KeyValue output = outputTopic.readKeyValue();
        assertEquals(output.key, "hello");
        assertEquals(output.value, 1L);
        assertTrue(outputTopic.isEmpty());
    }

    @Test
    public void shouldCountWords() {
        final List<String> inputLines = Arrays.asList(
                "Kafka Streams Examples",
                "Stream Sample",
                "Using Kafka Streams Test Utils"
        );
        final List<KeyValue<String, String>> inputRecords = inputLines.stream().map(v ->
                new KeyValue<String, String>(null, v)).collect(Collectors.toList());
        final Map<String, Long> expectedWordCounts = new HashMap<>();
        expectedWordCounts.put("kafka", 2L);
        expectedWordCounts.put("streams", 2L);
        expectedWordCounts.put("examples", 1L);
        expectedWordCounts.put("stream", 1L);
        expectedWordCounts.put("sample", 1L);
        expectedWordCounts.put("using", 1L);
        expectedWordCounts.put("test", 1L);
        expectedWordCounts.put("utils", 1L);
        inputTopic.pipeKeyValueList(inputRecords, Instant.ofEpochSecond(1L), Duration.ofMillis(1000L));
        final Map<String, Long> actualWordCounts = getOutputList();
        assertThat(actualWordCounts).containsAllEntriesOf(expectedWordCounts).hasSameSizeAs(expectedWordCounts);
    }

    private Map<String, Long> getOutputList() {
        final Map<String, Long> output = new HashMap<>();
        KeyValue outputRow;
        while (!outputTopic.isEmpty()) {
            outputRow = outputTopic.readKeyValue();
            output.put((String) outputRow.key, (Long) outputRow.value);
        }
        return output;
    }


    @AfterAll
    public static void tearDown() {
        testDriver.close();
    }
}
