# Kafka REST Consumer

This provides a REST API for consuming messages from Kafka.

The project can be easily built with Maven using `mvn install`.

# Prerequisites

- Kafka (available from `kafka.apache.org`)
- Java JDK 1.8

# Configuration

- `src/main/resources/kafkaConfig.properties` - edit the metadata broker list to point to your Kafka cluster

- `src/main/resources/postgres.properties` - edit to set postgres parameters

# Usage

- Run the webservice: `java -jar target/kafka-consumer-1.0-SNAPSHOT.jar`
- POST data into the API with curl: `curl -X POST http://localhost:8080/kafka-proxy/gnr/v1/consumer/start`