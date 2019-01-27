package com.mapfre.apis.gateway.security.config;

import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

//@Configuration
//@EnableWebFlux
public class CorsGlobalConfiguration implements WebFluxConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {
		corsRegistry.addMapping("/**").allowedOrigins("*").allowedHeaders("*").allowedMethods("POST","PUT","GET","OPTIONS","DELETE").allowCredentials(true).maxAge(3600);
	}
}