package com.marriott.rms.elevate.service.kafkaconsumer.spark;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.HasOffsetRanges;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.kafka010.OffsetRange;
import org.slf4j.LoggerFactory;

public class SparkStreamDirectConsumerService implements Serializable{

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(SparkStreamDirectConsumerService.class);

	private static final String SPARK_CONFIG = "sparkConfig";
	
	final int BATCH_INTERVAL = 30;

	final String TOPICS = "GNR";

	public SparkStreamDirectConsumerService() throws InterruptedException {

		LOG.info("Creating Spark Direct Stream..");
		SparkConf sparkConf = new SparkConf().setMaster("local[*]").setAppName(
				"GNRCount");


		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf,
				Durations.seconds(BATCH_INTERVAL));

		Map<String, Object> kafkaParams = new HashMap<>();
		ResourceBundle kafkaConsumerBundle = ResourceBundle
				.getBundle(SPARK_CONFIG);

		Enumeration<String> ckeys = kafkaConsumerBundle.getKeys();
		while (ckeys.hasMoreElements()) {
			String key = ckeys.nextElement();
			kafkaParams.put(key, kafkaConsumerBundle.getString(key));
		}

		final AtomicReference<OffsetRange[]> offsetRanges = new AtomicReference<>();

		JavaInputDStream<ConsumerRecord<String, String>> istream1 = KafkaUtils
				.createDirectStream(
						jssc,
						LocationStrategies.PreferConsistent(),
						ConsumerStrategies.<String, String> Subscribe(
								Arrays.asList(TOPICS), kafkaParams));

		LOG.info("Spark Direct Stream created..");

		JavaDStream<String> valueStream = istream1
				.transform(

						new Function<JavaRDD<ConsumerRecord<String, String>>, JavaRDD<ConsumerRecord<String, String>>>() {

							private static final long serialVersionUID = 1L;

							@Override
							public JavaRDD<ConsumerRecord<String, String>> call(
									JavaRDD<ConsumerRecord<String, String>> rdd) {
								OffsetRange[] offsets = ((HasOffsetRanges) rdd
										.rdd()).offsetRanges();
								offsetRanges.set(offsets);
								return rdd;
							}
						}).map(
						new Function<ConsumerRecord<String, String>, String>() {

							private static final long serialVersionUID = 1L;

							@Override
							public String call(ConsumerRecord<String, String> r) {
								return r.value();
							}
						});

		LOG.info("Transformed..");
		
		valueStream.foreachRDD(new VoidFunction<JavaRDD<String>>() {

			private static final long serialVersionUID = 1L;

			@Override
			public void call(JavaRDD<String> rdd) throws Exception {
				LOG.info("HERE>>>>>>>>>");
				try{
				rdd.saveAsTextFile("file:///Users/srmun072/hadoopfiles/");		
				}catch (Exception e){
					LOG.info("ERROR Writing to the file ");
					e.printStackTrace();
				}

				rdd.foreach(record -> {
					LOG.info("Value is "+record);
				});
			}
		     
		});

		jssc.start();
		jssc.awaitTermination();

	}


}