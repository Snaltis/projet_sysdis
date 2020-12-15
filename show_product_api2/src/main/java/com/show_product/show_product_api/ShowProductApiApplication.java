package com.show_product.show_product_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ShowProductApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShowProductApiApplication.class, args);
	}

}
