package com.mapfre.apis.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.security.oauth2.gateway.TokenRelayGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SpringBootApplication
//@EnableDiscoveryClient
public class GatewayApplication {

	@Autowired
	private TokenRelayGatewayFilterFactory filterFactory;

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
				.build();
	}

	@GetMapping("/")
	public String index(Model model, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
			@AuthenticationPrincipal OAuth2User oauth2User) {
		model.addAttribute("userName", oauth2User.getName());
		model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientName());
		model.addAttribute("userAttributes", oauth2User.getAttributes());
		return "index";
	}

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}