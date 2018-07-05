#Elevate GNR Producer

This pulls GNRs using CouchBase REST service and posts the messages to a REST Producer Service for Kafka.

The project can be easily built with Maven using `mvn install`.

# Prerequisites

- Java JDK 1.8

# Configuration

- `src/main/resources/couchbase.properties` - edit to point couchbase rest service
- `src/main/resources/kafkaService.properties` - edit to point kafka proxy rest service

# Usage

