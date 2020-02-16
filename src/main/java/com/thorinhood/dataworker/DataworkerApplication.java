package com.thorinhood.dataworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.thorinhood.dataworker")
public class DataworkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataworkerApplication.class, args);
	}

}
