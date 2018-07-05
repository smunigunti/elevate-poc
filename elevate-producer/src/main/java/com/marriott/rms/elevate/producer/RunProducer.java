package com.marriott.rms.elevate.producer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RunProducer {

	static ScheduledExecutorService timer = Executors
			.newSingleThreadScheduledExecutor();

	public static void main(String[] args) {

		Properties prop = readProperties("couchbase.properties");
		List<String> propCodes = Arrays.asList(prop.get("properties")
				.toString().split(","));

		final Integer interval = Integer.valueOf(prop.getProperty("interval"));

		Properties kprop = readProperties("kafkaService.properties");

		timer.scheduleAtFixedRate(
				() -> {
					
					String propCode = propCodes.get(new Random()
							.nextInt((propCodes.size() - 0) + 1) + 0);

					GNRProducer.produce(prop.getProperty("url"), propCode,
							prop.getProperty("startTime"),
							prop.getProperty("endTime"),
							kprop.getProperty("kafkaProxyService"));

				}, 0, interval, TimeUnit.SECONDS);

	}

	private static Properties readProperties(String propFileName) {
		Properties prop = new Properties();

		InputStream inputStream = RunProducer.class.getClassLoader()
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
		}
		return prop;
	}

}
