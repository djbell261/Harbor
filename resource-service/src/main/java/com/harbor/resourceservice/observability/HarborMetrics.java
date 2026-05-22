package com.harbor.resourceservice.observability;

import com.harbor.resourceservice.resource.repository.ResourceRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HarborMetrics {

	private static final Logger log = LoggerFactory.getLogger(HarborMetrics.class);

	private final MeterRegistry meterRegistry;
	private final ResourceRepository resourceRepository;
	private final int staleResourceDays;

	public HarborMetrics(
		MeterRegistry meterRegistry,
		ResourceRepository resourceRepository,
		@Value("${harbor.verification.stale-resource-days:30}") int staleResourceDays
	) {
		this.meterRegistry = meterRegistry;
		this.resourceRepository = resourceRepository;
		this.staleResourceDays = Math.max(staleResourceDays, 1);
		Gauge.builder("harbor.resources.stale.total", this, HarborMetrics::staleResourceCount)
			.description("Current number of public resources needing verification because lastVerifiedAt is stale or missing.")
			.register(meterRegistry);
	}

	public void recordVerificationSubmission(String reportType) {
		Counter.builder("harbor.verification.reports")
			.description("Total verification reports submitted.")
			.tags(Tags.of("report_type", tagValue(reportType)))
			.register(meterRegistry)
			.increment();
	}

	public void recordRateLimitRejection() {
		Counter.builder("harbor.verification.rate.limit.rejections")
			.description("Total verification report submissions rejected by rate limiting.")
			.register(meterRegistry)
			.increment();
	}

	public void recordAdminReview(String decision) {
		Counter.builder("harbor.admin.reviews")
			.description("Total admin verification review actions.")
			.tags(Tags.of("decision", tagValue(decision)))
			.register(meterRegistry)
			.increment();
	}

	public void recordResourceSearch(String endpoint) {
		Counter.builder("harbor.resource.search.requests")
			.description("Total public resource search requests.")
			.tags(Tags.of("endpoint", tagValue(endpoint)))
			.register(meterRegistry)
			.increment();
	}

	private double staleResourceCount() {
		Instant cutoff = Instant.now().minus(Duration.ofDays(staleResourceDays));
		try {
			return resourceRepository.countPublicStaleResources(cutoff);
		} catch (RuntimeException exception) {
			log.warn("event=metric_collection_failed metric=harbor_resources_stale_total reason={}", exception.getClass().getSimpleName());
			return Double.NaN;
		}
	}

	private String tagValue(String value) {
		return value == null || value.isBlank() ? "unknown" : value;
	}
}
