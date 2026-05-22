package com.harbor.resourceservice.verification.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VerificationReportRateLimiter {

	private final Map<String, Deque<Instant>> submissionsByIp = new ConcurrentHashMap<>();
	private final int maxSubmissions;
	private final Duration window;
	private final Clock clock;

	public VerificationReportRateLimiter(
		@Value("${harbor.verification.rate-limit.max-submissions:5}") int maxSubmissions,
		@Value("${harbor.verification.rate-limit.window-minutes:10}") int windowMinutes
	) {
		this(Math.max(maxSubmissions, 1), Duration.ofMinutes(Math.max(windowMinutes, 1)), Clock.systemUTC());
	}

	VerificationReportRateLimiter(int maxSubmissions, Duration window, Clock clock) {
		this.maxSubmissions = maxSubmissions;
		this.window = window;
		this.clock = clock;
	}

	public boolean tryAcquire(String ipAddress) {
		String key = ipAddress == null || ipAddress.isBlank() ? "unknown" : ipAddress.trim();
		Instant now = clock.instant();
		Deque<Instant> submissions = submissionsByIp.computeIfAbsent(key, ignored -> new ArrayDeque<>());
		synchronized (submissions) {
			prune(submissions, now);
			if (submissions.size() >= maxSubmissions) {
				return false;
			}
			submissions.addLast(now);
			return true;
		}
	}

	private void prune(Deque<Instant> submissions, Instant now) {
		Instant cutoff = now.minus(window);
		while (!submissions.isEmpty() && submissions.peekFirst().isBefore(cutoff)) {
			submissions.removeFirst();
		}
	}
}
