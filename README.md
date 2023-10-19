# Introduction to Kafka Streams
Presentation to a customer of introduction to Kafka Streams.
Based on slides https://docs.google.com/presentation/d/1bfXDT8kzQJ75xQFpoTASba65wJXMIUy5emglVaRluNA/edit?usp=sharing

First slides are discussional and no demo until **Demo time**.

# Demo - WordCountApp

## Intellij IDE App

### pom.xml
Start by showing a Java Maven based Kafka Streams application. Show the two critical dependencies of kafka-streams and kafka-streams-test-utils. 

### Topology
Show the create topology method where all the DSL Kafka Streams code resides. Explain each step, which ones are stateful which ones trigger a repartition.

### Main
Show the main method that basically depicts the structure of a Kafka Streams application.

### config
The config method for the configuration required by a Kafka Streams application. Discuss the two non production configuration points.

### Start CP Local
Before in case you need to reset just execute the Reset slide step. 

1. Create word-count-input topic
2. Start producer
3. Start Consumer on split panel (export paths if needed)
4. Start Kafka Streams app
5. Extract Topology and visualize
6. Write something and see consumer getting updated
7. Show the topics created for suppporting the app. Discuss their role and configuration.


### Commands Details

#### Reset Streams App and Clean Up Topics

```bash
kafka-streams-application-reset --application-id wordcount-app --input-topics word-count-input
kafka-topics --bootstrap-server localhost:9092 --delete --topic word-count-input
kafka-topics --bootstrap-server localhost:9092 --delete --topic word-count-output
```
#### Create Topic

kafka-topics --bootstrap-server localhost:9092 --topic word-count-input --create

#### Start Producer

```bash
kafka-console-producer --bootstrap-server localhost:9092 --topic word-count-input
```

#### Start Consumer

```bash
kafka-console-consumer --bootstrap-server localhost:9092 --topic word-count-output --from-beginning --property print.key=true --property key.separator=- --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```

#### List Topics - hide internals

```bash
echo 'topics list except starting ones:'
kafka-topics --bootstrap-server localhost:9092 --list --exclude-internal | sed /'^\_.*'/d | sed /'^connect-.*'/d | sed /'default_ksql_processing_log'/d
```

#### Consume on changelog

```bash
kafka-console-consumer --bootstrap-server localhost:9092 --topic wordcount-app-Counts-changelog --from-beginning --property print.key=true --property key.separator=- --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```