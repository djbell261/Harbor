package com.harbor.resourceservice.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI harborOpenApi() {
		return new OpenAPI()
			.info(new Info()
				.title("Harbor Resource Service API")
				.version("0.1.0")
				.description("""
					Public survival assistance resource APIs for Harbor.

					This API supports anonymous resource browsing, resource detail lookup,
					organization lookup, and anonymous community verification reports.
					Core endpoints are designed to be lightweight, privacy-conscious, and
					usable by a frontend during unstable network conditions.
					""")
				.contact(new Contact()
					.name("Harbor Project Maintainer")
					.email("maintainer@example.com")
					.url("https://example.com/harbor"))
				.license(new License()
					.name("License placeholder")
					.url("https://example.com/license")));
	}
}
