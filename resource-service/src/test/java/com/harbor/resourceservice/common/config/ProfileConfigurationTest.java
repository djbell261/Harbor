package com.harbor.resourceservice.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

class ProfileConfigurationTest {

	private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

	@Test
	void defaultProfileIsLocal() throws IOException {
		PropertySource<?> application = propertySource("application.yml");

		assertThat(application.getProperty("spring.profiles.default")).isEqualTo("local");
	}

	@Test
	void localAndDockerProfilesEnableDeveloperConveniences() throws IOException {
		PropertySource<?> local = propertySource("application-local.yml");
		PropertySource<?> docker = propertySource("application-docker.yml");

		assertThat(local.getProperty("spring.datasource.hikari.connection-init-sql"))
			.isEqualTo("SET harbor.seed_data.enabled = '${HARBOR_SEED_DATA_ENABLED:true}'");
		assertThat(local.getProperty("springdoc.api-docs.enabled"))
			.isEqualTo("${HARBOR_OPENAPI_ENABLED:true}");
		assertThat(docker.getProperty("spring.datasource.hikari.connection-init-sql"))
			.isEqualTo("SET harbor.seed_data.enabled = '${HARBOR_SEED_DATA_ENABLED:true}'");
		assertThat(docker.getProperty("springdoc.api-docs.enabled"))
			.isEqualTo("${HARBOR_OPENAPI_ENABLED:true}");
	}

	@Test
	void prodProfileUsesProductionSafeDefaults() throws IOException {
		PropertySource<?> prod = propertySource("application-prod.yml");

		assertThat(prod.getProperty("spring.datasource.url")).isEqualTo("${SPRING_DATASOURCE_URL}");
		assertThat(prod.getProperty("spring.datasource.hikari.connection-init-sql"))
			.isEqualTo("SET harbor.seed_data.enabled = '${HARBOR_SEED_DATA_ENABLED:false}'");
		assertThat(prod.getProperty("springdoc.api-docs.enabled"))
			.isEqualTo("${HARBOR_OPENAPI_ENABLED:false}");
		assertThat(prod.getProperty("springdoc.swagger-ui.enabled"))
			.isEqualTo("${HARBOR_OPENAPI_ENABLED:false}");
		assertThat(prod.getProperty("harbor.cors.allowed-origin-patterns"))
			.isEqualTo("${HARBOR_CORS_ALLOWED_ORIGIN_PATTERNS}");
	}

	private PropertySource<?> propertySource(String filename) throws IOException {
		List<PropertySource<?>> sources = loader.load(filename, new ClassPathResource(filename));

		assertThat(sources).hasSize(1);
		return sources.getFirst();
	}
}
