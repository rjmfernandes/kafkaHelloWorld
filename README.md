# Introduction to Kafka Streams
Introduction to Kafka Streams.

# Demo - WordCountApp

## Intellij IDE App

### pom.xml
The Java Maven based Kafka Streams application. Two critical dependencies of kafka-streams and kafka-streams-test-utils. 
The subdependencies includes the kafka client libraries for producer and consumer.

### WordCountApp

#### Topology
The create topology method where all the DSL Kafka Streams code resides. Check each step, which ones are stateful which ones trigger a repartition.

#### main
The main method that basically depicts the structure of a Kafka Streams application.

#### config
The config method for the configuration required by a Kafka Streams application. Discuss the two non production configuration points.

### Demo

1. Create word-count-input topic
2. Start producer
3. Start Consumer on split panel 
4. Start Kafka Streams app
5. Extract Topology and visualize
6. Write something and see consumer of output topic getting updated
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

#### List Topics 

```bash
echo 'topics list except starting ones:'
kafka-topics --bootstrap-server localhost:9092 --list --exclude-internal 
```

#### Consume on changelog

```bash
kafka-console-consumer --bootstrap-server localhost:9092 --topic wordcount-app-Counts-changelog --from-beginning --property print.key=true --property key.separator=- --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```

# Extra - WordCountProducer

Basic producer that writes to same input topic the message written as input requested.

# Extra - WordCountConsumer

Basic consumer consuming from same input topic.