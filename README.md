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
8. Optionally execute the consumer on the changelog topic.