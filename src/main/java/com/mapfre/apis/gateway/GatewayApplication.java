package com.mapfre.apis.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.security.oauth2.gateway.TokenRelayGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication
public class GatewayApplication {

	@Autowired
	private TokenRelayGatewayFilterFactory filterFactory;

	@Value("${spring.security.oauth2.client.registration.login-client.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.login-client.client-secret}")
	private String clientsecret;

	@Value("${identity-api-url}")
	private String identityApiUrl;
	@Value("${policy-api-url}")
	private String policyApiUrl;
	@Value("${claims-api-url}")
	private String claimsApiUrl;
	@Value("${billing-api-url}")
	private String billingApiUrl;
	@Value("${b2c-accounts-api-url}")
	private String b2cAccountsApiUrl;
	@Value("${address-normalizer-url}")
	private String addressNormalizerUrl;
	@Value("${sso-tile-url}")
	private String ssoTileUrl;
	@Value("${host-allowed}")
	private String host_allowed;

	@Value("${client_credentials_id}")
	private String client_credentials_id;
	@Value("${client_credentials_secret}")
	private String client_credentials_secret;

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				// Identity-API
				.route("identity", r -> r.path("/identity-api/**")
						.filters(f -> f.rewritePath("/identity-api/(?<segment>.*)", "/${segment}")
								.filter(filterFactory.apply())
								.setResponseHeader("Access-Control-Allow-Origin", host_allowed))
						.uri(identityApiUrl))
				// B2C-Account-API
				.route("b2c-account", r -> r.path("/b2c-account-api/**")
						.filters(f -> f.rewritePath("/b2c-account-api/(?<segment>.*)", "/${segment}")
								.filter(filterFactory.apply())
								.setResponseHeader("Access-Control-Allow-Origin", host_allowed))
						.uri(b2cAccountsApiUrl))
				// Personal-Policy-API
				.route("personal-policy", r -> r.path("/personal-policy-api/**")
						.filters(f -> f.rewritePath("/personal-policy-api/(?<segment>.*)", "/${segment}")
								.filter(filterFactory.apply())
								.setResponseHeader("Access-Control-Allow-Origin", host_allowed))
						.uri(policyApiUrl))
				// Claims-API
				.route("claims", r -> r.path("/claims-api/**")
						.filters(f -> f.rewritePath("/claims-api/(?<segment>.*)", "/${segment}")
								.filter(filterFactory.apply())
								.setResponseHeader("Access-Control-Allow-Origin", host_allowed))
						.uri(claimsApiUrl))
				// Billing-API
				.route("billing", r -> r.path("/billing-api/**")
						.filters(f -> f.rewritePath("/billing-api/(?<segment>.*)", "/${segment}")
								.filter(filterFactory.apply())
								.setResponseHeader("Access-Control-Allow-Origin", host_allowed))
						.uri(billingApiUrl))
				// Name-Address-Normalizer-API
				.route("name-address-normalizer", r -> r.path("/name-address-normalizer-api/**")
						.filters(f -> f.rewritePath("/name-address-normalizer-api/(?<segment>.*)", "/${segment}")
								.filter(filterFactory.apply())
								.setResponseHeader("Access-Control-Allow-Origin", host_allowed))
						.uri(addressNormalizerUrl))
				// Authorization and Token Endpoint
				.route("auth", r -> r.path("/auth/**")
						.filters(f -> f.rewritePath("/auth/(?<segment>.*)", "/${segment}").filter(filterFactory.apply())
								.addRequestParameter("client_id", clientId)// .rewriteResponseHeader("Access-Control-Allow-Origin",
																			// "*","*")
								.addRequestParameter("client_secret", clientsecret)
								.setResponseHeader("Access-Control-Allow-Origin", host_allowed))
						.uri(ssoTileUrl))
				// Authorization and Token Endpoint
				.route("auth-client", r -> r.path("/auth-client/**")
						.filters(f -> f.rewritePath("/auth-client/(?<segment>.*)", "/${segment}")
								.filter(filterFactory.apply()).addRequestParameter("client_id", client_credentials_id)// .rewriteResponseHeader("Access-Control-Allow-Origin",
								// "*","*")
								.addRequestParameter("client_secret", client_credentials_secret)
								.setResponseHeader("Access-Control-Allow-Origin", host_allowed))
						.uri(ssoTileUrl))
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}