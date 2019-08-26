package com.wangwei.rabbitmqspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class RabbitmqSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqSpringApplication.class, args);
	}

}
