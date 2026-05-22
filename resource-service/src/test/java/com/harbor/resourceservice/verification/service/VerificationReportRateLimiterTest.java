package com.harbor.resourceservice.verification.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class VerificationReportRateLimiterTest {

	@Test
	void allowsRequestsWithinLimit() {
		MutableClock clock = new MutableClock(Instant.parse("2026-05-22T00:00:00Z"));
		VerificationReportRateLimiter limiter = new VerificationReportRateLimiter(5, Duration.ofMinutes(10), clock);

		assertThat(limiter.tryAcquire("203.0.113.10")).isTrue();
		assertThat(limiter.tryAcquire("203.0.113.10")).isTrue();
		assertThat(limiter.tryAcquire("203.0.113.10")).isTrue();
		assertThat(limiter.tryAcquire("203.0.113.10")).isTrue();
		assertThat(limiter.tryAcquire("203.0.113.10")).isTrue();
	}

	@Test
	void blocksRequestsOverLimitUntilWindowExpires() {
		MutableClock clock = new MutableClock(Instant.parse("2026-05-22T00:00:00Z"));
		VerificationReportRateLimiter limiter = new VerificationReportRateLimiter(2, Duration.ofMinutes(10), clock);

		assertThat(limiter.tryAcquire("203.0.113.10")).isTrue();
		assertThat(limiter.tryAcquire("203.0.113.10")).isTrue();
		assertThat(limiter.tryAcquire("203.0.113.10")).isFalse();

		clock.advance(Duration.ofMinutes(11));

		assertThat(limiter.tryAcquire("203.0.113.10")).isTrue();
	}

	private static class MutableClock extends Clock {

		private Instant instant;

		MutableClock(Instant instant) {
			this.instant = instant;
		}

		void advance(Duration duration) {
			instant = instant.plus(duration);
		}

		@Override
		public ZoneId getZone() {
			return ZoneId.of("UTC");
		}

		@Override
		public Clock withZone(ZoneId zone) {
			return this;
		}

		@Override
		public Instant instant() {
			return instant;
		}
	}
}
