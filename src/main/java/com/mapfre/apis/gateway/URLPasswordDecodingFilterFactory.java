package com.mapfre.apis.gateway;

import java.net.URI;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.util.UriComponentsBuilder;

public class URLPasswordDecodingFilterFactory extends AbstractNameValueGatewayFilterFactory {

	public GatewayFilter apply() {
		return apply((NameValueConfig) null);
	}

	@Override
	public GatewayFilter apply(NameValueConfig config) {
		return (exchange, chain) -> {
			URI uri = exchange.getRequest().getURI();
			String originalQuery = uri.getRawQuery();
			Optional<String> decodedPassword = Arrays.stream(originalQuery.split("&")).filter(str -> str.contains("password="))
					.map(password -> new String(Base64.getDecoder().decode(password.split("=")[1]))).findFirst();
			
			List<String> params = new java.util.ArrayList<>(Arrays.asList(originalQuery.split("&")));
			params.removeIf(p -> p.contains("password="));
			
			try {
				URI newUri = UriComponentsBuilder.fromUri(uri)
						.replaceQueryParam("password", decodedPassword.get()).build(true).toUri();
				
				ServerHttpRequest request = exchange.getRequest().mutate().uri(newUri).build();

				return chain.filter(exchange.mutate().request(request).build());
			} catch (RuntimeException ex) {
				throw new IllegalStateException("Invalid URI query: \"" +  "\"");
			}
		};
	}
}
