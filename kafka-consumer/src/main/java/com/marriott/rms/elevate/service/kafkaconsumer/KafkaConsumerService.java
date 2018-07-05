package com.marriott.rms.elevate.service.kafkaconsumer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.marriott.rms.elevate.domain.kafkaconsumer.SMSMessage;


@Service
public class KafkaConsumerService {

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(KafkaConsumerService.class);
	
	private static final Gson gson = new Gson();

	private KafkaConsumer<String, String> consumer;

	public Boolean isRunning = Boolean.FALSE;

	final String TOPIC = "GNR";
	
	final String JSON_FORMAT = MediaType.APPLICATION_JSON;

	private BasicDataSource connectionPool;

	public KafkaConsumerService() throws SQLException, URISyntaxException {

		LOG.info("KafkaConsumerService - start");

		Properties kafkaConsumerProps = readProperties("kafkaConfig.properties");
		consumer = new KafkaConsumer<>(kafkaConsumerProps);
		consumer.subscribe(Arrays.asList(TOPIC));

		setupDB();
		LOG.info("DB Connection is setup");

		consume();
		LOG.info("Consumer created and running Successfully!");
	}

	public void consume() throws SQLException {
		LOG.info("Consumer Invoked");
		int count = 0;
		try {
			isRunning = Boolean.TRUE;
			while (true) {
				LOG.info("Polling...");
				ConsumerRecords<String, String> records = consumer
						.poll(Long.MAX_VALUE);
				LOG.info("Found "+records.count()+" records");
				
				insertData(records);
				
				count = count+records.count();
				if (count>50){
					final int cnt = count;
					LOG.info("Sending SMS..");
				    new Thread(() -> sms(cnt)).start();
					count=0;
				}

			}
		} catch (WakeupException e) {
			LOG.info("Consumer wakeup exception..Stopping consumer");
			isRunning = Boolean.FALSE;
		} finally {
			consumer.close();
			LOG.info("Consumer Stopped");
		}
	}

	public void shutdown() {
		consumer.wakeup();
	}

	private void insertData(ConsumerRecords<String, String> records) {
		
		Connection conn =  null;
		Statement stmt = null;
		try {

			LOG.info("Inserting "+records.count()+" to Postgres..");
			
			StringBuilder sb = new StringBuilder();
			for (ConsumerRecord<String, String> record : records) {
				if (isJSONValid(record.value()))
					sb.append("(uuid_generate_v1(),'").append(record.value()).append("'),");
				else{
					LOG.info("Invalid record: "+record.value());
				}
			}
			
			String values = sb.deleteCharAt(sb.length()-1).toString();

			conn = connectionPool.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			String sql = "INSERT INTO GNR VALUES "+values;
			
			//LOG.info("Ready to execute the insert statement  \n"+sql);
			int count = stmt.executeUpdate(sql);
			
			conn.commit();
			LOG.info("Data Inserted into Postgres: "+count);

		} catch (Exception e) {
			LOG.info("Error While inserting data into DB: " + e.getMessage(), e);
		}finally{
			try{
			if (stmt!=null) stmt.close();
			if (conn!=null) conn.close();
			}catch (Exception e) {
				LOG.info("Error While closing connection: " + e.getMessage(), e);
			}
			
		}

	}

	private void setupDB() throws URISyntaxException, SQLException {
		
		Properties prop = readProperties("postgres.properties");

		String dbUrl = prop.getProperty("dbUrl");
		connectionPool = new BasicDataSource();
		connectionPool.setUsername(prop.getProperty("user"));
		connectionPool.setPassword(prop.getProperty("password"));
		connectionPool.setDriverClassName("org.postgresql.Driver");
		connectionPool.setUrl(dbUrl);
		connectionPool.setInitialSize(1);
		
		Connection connection = connectionPool.getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("CREATE EXTENSION IF NOT EXISTS  \"uuid-ossp\"");
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS GNR ( id UUID PRIMARY KEY DEFAULT uuid_generate_v1(), data jsonb)");
		stmt.close();
		connection.close();
	}
	
	public static boolean isJSONValid(String jsonInString) {
	      try {
	          gson.fromJson(jsonInString, Object.class);
	          return true;
	      } catch(com.google.gson.JsonSyntaxException ex) { 
	          return false;
	      }
	  }



	private Properties readProperties(String propFileName) {
		Properties prop = new Properties();


		InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream(propFileName);

		try {
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '"
						+ propFileName + "' not found in the classpath");
			}
		} catch (IOException e) {
			e.printStackTrace();
			LOG.info("Error While reading property file: " + e.getMessage(), e);
		}
		return prop;
	}
	
	private void sms(int count){
		
		Properties prop = readProperties("smsConfig.properties");
		
		SMSMessage message = new SMSMessage("8606806235@txt.att.net", "GNR Count", "GNR Current count "+count);
		
		Builder target = ClientBuilder.newClient()
				.target(prop.getProperty("smsUrl"))
				.request(JSON_FORMAT)
				.header("Content-Type", "application/json");

		
		Response resp = target.post(Entity.entity(gson.toJson(message), JSON_FORMAT));
		
		LOG.info("Finished Sending SMS "+resp.getStatus());
		
	}

}
