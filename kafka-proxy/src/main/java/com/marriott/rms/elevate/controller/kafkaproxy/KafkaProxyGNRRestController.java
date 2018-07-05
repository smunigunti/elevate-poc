package com.marriott.rms.elevate.controller.kafkaproxy;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marriott.rms.elevate.service.kafkaproxy.KafkaProducerService;

@RestController
@RequestMapping("/gnr/v1")
public class KafkaProxyGNRRestController {

	@Autowired
	KafkaProducerService kafkaProducerService;

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(KafkaProxyGNRRestController.class);
	private static final String GNR_TOPIC_NAME = "GNR";
	private final GsonBuilder gsonBuilder = new GsonBuilder();

	@RequestMapping(value = "/produce", method = RequestMethod.POST)
	public ResponseEntity<String> produce(@RequestBody String body,
			UriComponentsBuilder ucBuilder) {

		//LOG.info("post body " + body);

		List<String> messages = Arrays.asList(body.split("\n"));
		LOG.info("Number of messages to send to Kafka  " + messages.size());
		
		List<Future<RecordMetadata>> metadata = kafkaProducerService
				.sendMessage(GNR_TOPIC_NAME, messages);

		LOG.info("Sent to Kafka " + metadata.size() + " messages");
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<String>(metadata.size()
				+ "Message Sent to Kafka", headers, HttpStatus.CREATED);
	}

	public void setKafkaProducerService(
			KafkaProducerService kafkaProducerService) {
		this.kafkaProducerService = kafkaProducerService;
	}

	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public String ping(@RequestParam MultiValueMap<String, String> requestParams) {

		Gson gson = gsonBuilder.setPrettyPrinting().create();
		LOG.info("at ping: " + requestParams.toString());
		return gson.toJson(requestParams);
	}

}
