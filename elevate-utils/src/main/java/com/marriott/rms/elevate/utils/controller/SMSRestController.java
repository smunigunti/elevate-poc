package com.marriott.rms.elevate.utils.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marriott.rms.elevate.utils.sms.SMSService;

@RestController
@RequestMapping("/sms/v1")
public class SMSRestController {

	@Autowired
	SMSService smsService;

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(SMSRestController.class);

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public ResponseEntity<String> start(@RequestBody String body,
			UriComponentsBuilder ucBuilder) throws SQLException,
			JsonProcessingException, IOException {

		ObjectMapper mapper = new ObjectMapper();

		JsonNode message = mapper.readTree(body);
		JsonNode toList = message.get("to");
		JsonNode subject = message.get("subject");
		JsonNode content = message.get("message");

		LOG.info(toList + " " + subject + " " + content);

		List<String> sendList = Arrays.asList(toList.toString().split(":"));

		smsService.sendEmail(sendList, subject.toString(), content.toString());
		String returnMessage = "Sent the message";

		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<String>(returnMessage, headers,
				HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public String ping(@RequestParam MultiValueMap<String, String> requestParams) {

		LOG.info("at ping: " + requestParams.toString());
		return "SMS Controller up and running";
	}

}
