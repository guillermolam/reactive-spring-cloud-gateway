package com.mapfre.apis.gateway;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
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
			if (originalQuery != null) {
				List<String> params = new java.util.ArrayList<>(Arrays.asList(originalQuery.split("&")));
				params.removeIf(p -> p.contains("password="));
				try {
					URI newUri = UriComponentsBuilder.fromUri(uri).replaceQueryParam("password",
							URLEncoder.encode(extractField(originalQuery).get(), "UTF-8")).build(true).toUri();
					ServerHttpRequest request = exchange.getRequest().mutate().uri(newUri).build();
					return chain.filter(exchange.mutate().request(request).build());
				} catch (RuntimeException | UnsupportedEncodingException e) {
					throw new IllegalStateException("Invalid URI query: \"" + "\"");
				}
			}
			return chain.filter(exchange.mutate().request(exchange.getRequest()).build());
		};
	}

	public Optional<String> extractField(String originalQuery) {
		return Arrays.stream(originalQuery.split("&")).filter(str -> str.contains("password="))
				.map(password -> decode(password.split("=")[1])).findFirst();
	}

	private String decode(String aString) {
		try {
			return new String(Base64.getDecoder().decode(aString));
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Invalid URI query");
		}
	}
}
