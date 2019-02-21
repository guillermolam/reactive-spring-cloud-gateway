package com.mapfre.apis.gateway.security.config;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
public class CORSFilter {

	private static final String ALLOWED_HEADERS = "DNT,"
			+ "X-CustomHeader,"
			+ "Keep-Alive,"
			+ "User-Agent,"
			+ "X-Requested-With,"
			+ "If-Modified-Since,"
			+ "Cache-Control,Content-Type,"
			+ "Content-Range,"
			+ "Range,"
			+ "authorization,"
			+ "access-control-allow-origin,"
			+ "access-control-allow-headers,"
			+ "access-control-allow-methods";
	
	private static final String ALLOWED_METHODS = "GET, PUT, POST, DELETE, OPTIONS";
	private static final String MAX_AGE = "1728000";

	@Value("${allowedOrigins}")
	private String[] allowedOrigins;

	@Bean
	public WebFilter corsFilter() {
		return (ServerWebExchange ctx, WebFilterChain chain) -> {
			ServerHttpRequest request = ctx.getRequest();
			Stream<String> origins = Arrays.asList(allowedOrigins).stream();
			if (CorsUtils.isCorsRequest(request)) {
				ServerHttpResponse response = ctx.getResponse();
				HttpHeaders headers = response.getHeaders();

				Optional<String> allowedOrigin = getOrigin(request, origins);

				headers.add("Access-Control-Allow-Origin", allowedOrigin.isPresent() ? allowedOrigin.get() : StringUtils.EMPTY );
				headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
				headers.add("Access-Control-Max-Age", MAX_AGE);
				headers.add("Access-Control-Allow-Headers", ALLOWED_HEADERS);

				if (request.getMethod() == HttpMethod.OPTIONS) {
					response.setStatusCode(HttpStatus.OK);
					return Mono.empty();
				}
			}
			return chain.filter(ctx);
		};
	}

	public static Optional<String> getOrigin(ServerHttpRequest request, Stream<String> origins) {
		Optional<String> allowedOrigin = origins
				.filter(element -> request.getHeaders().get("Origin").stream().anyMatch(el -> el.contains(element)))
				.findFirst();
		return allowedOrigin;
	}
}