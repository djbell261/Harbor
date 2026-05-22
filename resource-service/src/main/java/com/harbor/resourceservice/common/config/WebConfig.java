package com.harbor.resourceservice.common.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final String[] allowedOriginPatterns;

	public WebConfig(
		@Value("${harbor.cors.allowed-origin-patterns:}") String allowedOriginPatterns
	) {
		this.allowedOriginPatterns = Arrays.stream(allowedOriginPatterns.split(","))
			.map(String::trim)
			.filter(value -> !value.isBlank())
			.toArray(String[]::new);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		if (allowedOriginPatterns.length == 0) {
			return;
		}

		registry.addMapping("/api/**")
			.allowedOriginPatterns(allowedOriginPatterns)
			.allowedMethods("GET", "POST", "OPTIONS")
			.allowedHeaders("*");
	}
}
