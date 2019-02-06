package com.mapfre.apis.gateway.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//		http.csrf().disable().authorizeExchange().pathMatchers("/auth/**").permitAll();
//		return http.build();
		http.csrf().disable().cors().disable().authorizeExchange().pathMatchers("/actuator/**").permitAll()
		.and().authorizeExchange().pathMatchers("/auth/**").permitAll()
		.and().authorizeExchange().pathMatchers("/auth-client/**").permitAll()
		.and().authorizeExchange().pathMatchers(HttpMethod.OPTIONS,"/**").permitAll()
		.and().authorizeExchange().pathMatchers("/**").hasAuthority("SCOPE_openid").anyExchange().authenticated().and().oauth2ResourceServer().jwt();
		return http.build();
	}

}
