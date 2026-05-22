package com.harbor.resourceservice.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		long startedAt = System.nanoTime();
		try {
			filterChain.doFilter(request, response);
		} finally {
			long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
			int status = response.getStatus();
			if (status >= 500) {
				log.error("event=http_request_completed method={} path={} status={} durationMs={} correlationId={}",
					request.getMethod(), request.getRequestURI(), status, durationMs, MDC.get(CorrelationIdFilter.MDC_KEY));
			} else if (status >= 400) {
				log.warn("event=http_request_completed method={} path={} status={} durationMs={} correlationId={}",
					request.getMethod(), request.getRequestURI(), status, durationMs, MDC.get(CorrelationIdFilter.MDC_KEY));
			} else {
				log.info("event=http_request_completed method={} path={} status={} durationMs={} correlationId={}",
					request.getMethod(), request.getRequestURI(), status, durationMs, MDC.get(CorrelationIdFilter.MDC_KEY));
			}
		}
	}
}
