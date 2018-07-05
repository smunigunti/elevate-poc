# Kafka REST Proxy

This provides a REST API for posting messages to Kafka.

The project can be easily built with Maven using `mvn install`.

# Prerequisites

- Kafka (available from `kafka.apache.org`)
- Java JDK 1.8

# Configuration

- `src/main/resources/kafkaConfig.properties` - edit the metadata broker list to point to your Kafka cluster

# Usage

- Run the webservice: `java -jar target/kafka-proxy-1.0-SNAPSHOT.jar`
- POST data into the API with curl: `curl -X POST http://localhost:8080/kafka-proxy/gnr/v1/produce`