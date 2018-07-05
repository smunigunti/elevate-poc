
package com.marriott.rms.elevate.utils;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableAutoConfiguration
@ComponentScan("com.marriott.rms.elevate")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

