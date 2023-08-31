package com.example.food.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@EnableJpaRepositories
@EnableAutoConfiguration
public class FoodDeliveryUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodDeliveryUserApplication.class, args);
	}

}
