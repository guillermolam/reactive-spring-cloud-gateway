package com.mapfre.customerportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@EnableOAuth2Sso
@SpringBootApplication
public class CustomerPortalBffApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerPortalBffApplication.class, args);
	}
}
