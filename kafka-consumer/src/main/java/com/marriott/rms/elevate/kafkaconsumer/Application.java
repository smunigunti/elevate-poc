
package com.marriott.rms.elevate.kafkaconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Run the kafka proxy service as a Spring application
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("com.marriott.rms.elevate")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
