package com.marriott.rms.elevate.service.kafkaproxy;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(KafkaProducerService.class);
	private static final String KAFKA_CONFIG = "kafkaConfig";

	private KafkaProducer<String, String> kafkaProducer;

	public KafkaProducerService() {

		LOG.info("KafkaProducerService - start");
		Properties kafkaProducerProps = new Properties();
		ResourceBundle kafkaProducerBundle = ResourceBundle
				.getBundle(KafkaProducerService.KAFKA_CONFIG);

		Enumeration<String> keys = kafkaProducerBundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			kafkaProducerProps.put(key, kafkaProducerBundle.getString(key));
		}

		kafkaProducer = new KafkaProducer<>(kafkaProducerProps);
		LOG.info(" Kafka producer is created");
	}

	public Future<RecordMetadata> sendMessage(String topicName, String message) {
		LOG.info("Sending message to Kafka");
		return kafkaProducer.send(new ProducerRecord<String, String>(topicName,
				message));
	}

	public List<Future<RecordMetadata>> sendMessage(String topicName,
			List<String> messages) {
		List<Future<RecordMetadata>> list = new ArrayList<Future<RecordMetadata>>();
		LOG.info("Sending "+messages.size()+" messages to Kafka");
		messages.stream().forEach(message->{
			if (message.trim().length()>0){
				list.add(kafkaProducer.send(new ProducerRecord<String, String>(
						topicName, message)));
			}
		});		
		return list;
	}

	protected void setKafkaProducer(KafkaProducer<String, String> producer) {
		this.kafkaProducer = producer;
	}

}
