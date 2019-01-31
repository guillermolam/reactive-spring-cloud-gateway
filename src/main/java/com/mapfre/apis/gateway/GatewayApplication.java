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

	@Value("${client-id}")
	private String clientId;

	@Value("${client-secret}")
	private String clientsecret;

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("resource",
						r -> r.path("/resource").filters(f -> f.filter(filterFactory.apply()))
								.uri("https://resource-server.apps.nonprod.us-east-1.aws.pcf.mapfreusa.com"))
				// Identity-API
				.route("identity", r -> r.path("/identity-api/**").filters(
						f -> f.rewritePath("/identity-api/(?<segment>.*)", "/${segment}").filter(filterFactory.apply()))
						.uri("https://identity-api.apps.nonprod.us-east-1.aws.pcf.mapfreusa.com"))
				// B2C-Account-API
				.route("b2c-account",
						r -> r.path("/b2c-account-api/**")
								.filters(f -> f.rewritePath("/b2c-account-api/(?<segment>.*)", "/${segment}")
										.filter(filterFactory.apply()))
								.uri("https://b2c-account-api.apps.nonprod.us-east-1.aws.pcf.mapfreusa.com"))
				// Personal-Policy-API
				.route("personal-policy",
						r -> r.path("/personal-policy-api/**")
								.filters(f -> f.rewritePath("/personal-policy-api/(?<segment>.*)", "/${segment}")
										.filter(filterFactory.apply()))
								.uri("https://personal-policy-api.apps.nonprod.us-east-1.aws.pcf.mapfreusa.com"))
				// Claims-API
				.route("claims", r -> r.path("/claims-api/**").filters(
						f -> f.rewritePath("/claims-api/(?<segment>.*)", "/${segment}").filter(filterFactory.apply()))
						.uri("https://claims-api.apps.nonprod.us-east-1.aws.pcf.mapfreusa.com"))
				// Billing-API
				.route("billing", r -> r.path("/billing-api/**").filters(
						f -> f.rewritePath("/billing-api/(?<segment>.*)", "/${segment}").filter(filterFactory.apply()))
						.uri("https://billing-api.apps.nonprod.us-east-1.aws.pcf.mapfreusa.com"))
				// Authorization and Token Endpoint
				.route("auth",
						r -> r.path("/auth/**")
								.filters(f -> f.rewritePath("/auth/(?<segment>.*)", "/${segment}")
										.filter(filterFactory.apply()).addRequestParameter("client_id", clientId)
										.addRequestParameter("client_secret", clientsecret))
								.uri("https://external-dev.login.sys.nonprod.us-east-1.aws.pcf.mapfreusa.com"))
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}