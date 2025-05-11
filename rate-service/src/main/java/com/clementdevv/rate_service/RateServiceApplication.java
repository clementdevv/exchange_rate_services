package com.clementdevv.rate_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.clementdevv.rate_service.config.ExchangeApiProperties;

@SpringBootApplication
@EnableConfigurationProperties(ExchangeApiProperties.class)
public class RateServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RateServiceApplication.class, args);
	}

}
