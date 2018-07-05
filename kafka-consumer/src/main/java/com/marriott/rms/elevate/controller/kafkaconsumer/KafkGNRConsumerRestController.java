package com.marriott.rms.elevate.controller.kafkaconsumer;

import java.sql.SQLException;

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
import com.marriott.rms.elevate.service.kafkaconsumer.KafkaConsumerService;

@RestController
@RequestMapping("/gnr/v1")
public class KafkGNRConsumerRestController {

	@Autowired
	KafkaConsumerService kafkaConsumerService;

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(KafkGNRConsumerRestController.class);

	private final GsonBuilder gsonBuilder = new GsonBuilder();

	@RequestMapping(value = "/consumer/start", method = RequestMethod.POST)
	public ResponseEntity<String> start(@RequestBody String body,
			UriComponentsBuilder ucBuilder) throws SQLException {

		String returnMessage = null;
		if (!kafkaConsumerService.isRunning) {
			kafkaConsumerService.consume();
			returnMessage = "Consumer started Successfully";
		} else {
			returnMessage = "Consumer already Running";
		}

		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<String>(returnMessage, headers,
				HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/consumer/stop", method = RequestMethod.POST)
	public ResponseEntity<String> stop(@RequestBody String body,
			UriComponentsBuilder ucBuilder) {

		String returnMessage = null;
		if (kafkaConsumerService.isRunning) {
			kafkaConsumerService.shutdown();
			returnMessage = "Consumer stopped Successfully";
		} else {
			returnMessage = "Consumer already stopped";
		}

		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<String>(returnMessage, headers,
				HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public String ping(@RequestParam MultiValueMap<String, String> requestParams) {

		Gson gson = gsonBuilder.setPrettyPrinting().create();
		LOG.info("at ping: " + requestParams.toString());
		return gson.toJson(requestParams);
	}

}
