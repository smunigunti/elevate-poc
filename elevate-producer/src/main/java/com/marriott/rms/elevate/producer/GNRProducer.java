package com.marriott.rms.elevate.producer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GNRProducer {

	static final String JSON_FORMAT = MediaType.APPLICATION_JSON;

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(GNRProducer.class);

	public static void produce(String... args) {

		String url = args[0];
		String propCode = args[1];
		String startTime = args[2];
		String endTime = args[3];
		String kafkaServiceUrl = args[4];

		ObjectMapper mapper = new ObjectMapper();
		


		LOG.info(url + " " + propCode + " " + startTime + " " + endTime);

		
		Response response = ClientBuilder.newClient().target(url)
				.path(propCode.trim()).path("allreservations")
				.queryParam("lastupdatetimefrom", startTime.trim())
				.queryParam("lastupdatetimeto", endTime.trim())
				.queryParam("responseType", "Q")
				.request(MediaType.APPLICATION_JSON).get();

		LOG.info("Response "+response.getStatus());

		if (response.getStatus()==204)
			return;
		
		List<String> gnrJson = new ArrayList<>();

		JsonNode jsonTree = null;
		try {
			jsonTree = mapper.readTree(response.readEntity(String.class));
		} catch (JsonProcessingException e) {
			LOG.info("Error Reading/Mapping the FNR data to Object: "
					+ e.getMessage(),e);
		} catch (IOException e) {
			LOG.info("Error Reading/Mapping the FNR data to Object: "
					+ e.getMessage(),e);
		}

		if (jsonTree != null && jsonTree.isArray()) {
			LOG.info("GNR Count: " + jsonTree.size() + " for property code "
					+ propCode);
			jsonTree.elements().forEachRemaining(element -> {
				gnrJson.add(element.get("property").toString());
			});
		}

		//gnrJson.stream().forEach(gnr -> LOG.info(gnr));
		LOG.info("Finished Reading from Couchbase--Writing to Kafka REST Proxy");
		try{
		write2Kafka(gnrJson,kafkaServiceUrl);
		}catch(Exception e){
			e.printStackTrace();
		}
		

	}

	private static void write2Kafka(List<String> messages, String kafkaServiceUrl) {
		
		messages = messages.subList(0, new Random().nextInt(20)); //to control the volume
		
		LOG.info("Count after trim: "+messages.size() );
		Builder target = ClientBuilder.newClient()
				.target(kafkaServiceUrl)
				.request(JSON_FORMAT)
				.header("Content-Type", "application/json");

		StringBuilder sb = new StringBuilder();
		messages.stream().forEach(				
				message -> {
					sb.append(message).append("\n");
				});
		Response resp = target.post(Entity.entity(
				sb.toString(), JSON_FORMAT));
		LOG.info("Finished Posting "+ messages.size()+" messages to Kafka-Proxy. Status: "+resp.getStatus());
	}

}
