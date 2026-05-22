package com.harbor.resourceservice.resource.service;

import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResourceFreshnessService {

	private final int staleResourceDays;

	public ResourceFreshnessService(
		@Value("${harbor.verification.stale-resource-days:30}") int staleResourceDays
	) {
		this.staleResourceDays = Math.max(staleResourceDays, 1);
	}

	public int staleResourceDays() {
		return staleResourceDays;
	}

	public boolean isStale(Instant lastVerifiedAt, Instant now) {
		return lastVerifiedAt == null
			|| lastVerifiedAt.isBefore(now.minus(Duration.ofDays(staleResourceDays)));
	}
}
