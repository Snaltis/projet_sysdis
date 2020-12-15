package com.service_cart.service_cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceCartApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceCartApplication.class, args);
	}

}
