package com.service_inventory.get_stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GetStockApplication {

	public static void main(String[] args) {
		SpringApplication.run(GetStockApplication.class, args);
	}

}
