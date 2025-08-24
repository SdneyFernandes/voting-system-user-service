package br.com.voting_system_user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDiscoveryClient  
@SpringBootApplication
public class VotingSystemUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotingSystemUserServiceApplication.class, args);
	}

}