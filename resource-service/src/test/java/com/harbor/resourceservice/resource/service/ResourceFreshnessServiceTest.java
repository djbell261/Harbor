package com.harbor.resourceservice.resource.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class ResourceFreshnessServiceTest {

	private final ResourceFreshnessService service = new ResourceFreshnessService(30);

	@Test
	void marksMissingVerificationAsStale() {
		assertThat(service.isStale(null, Instant.parse("2026-05-22T00:00:00Z"))).isTrue();
	}

	@Test
	void marksResourcesOlderThanThresholdAsStale() {
		Instant now = Instant.parse("2026-05-22T00:00:00Z");

		assertThat(service.isStale(Instant.parse("2026-04-20T23:59:59Z"), now)).isTrue();
		assertThat(service.isStale(Instant.parse("2026-04-22T00:00:00Z"), now)).isFalse();
		assertThat(service.isStale(Instant.parse("2026-05-10T00:00:00Z"), now)).isFalse();
	}
}
