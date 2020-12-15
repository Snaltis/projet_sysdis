package com.get_livraisons_api.get_livraisons;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GetLivraisonsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GetLivraisonsApplication.class, args);
	}

}
