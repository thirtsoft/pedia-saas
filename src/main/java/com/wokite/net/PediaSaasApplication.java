package com.wokite.net;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PediaSaasApplication {

	public static void main(String[] args) {
		SpringApplication.run(PediaSaasApplication.class, args);
	}

}